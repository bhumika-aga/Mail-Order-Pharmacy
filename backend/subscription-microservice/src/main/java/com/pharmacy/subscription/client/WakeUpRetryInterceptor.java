package com.pharmacy.subscription.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

/**
 * Waits for another service that is still starting up, e.g. a free Render
 * instance waking from sleep (about a minute), instead of failing the call at
 * once. Retries with growing pauses (1s, 2s, 4s ... at most 10s) until
 * {@code services.http.wake-up-wait} has passed. Off when that is 0 (the default).
 * <p>
 * "Still starting" means: the connection was refused, the reply was an HTML page
 * (a platform loading page: these APIs only return JSON), or, for calls that are
 * safe to repeat, a 502/503/504 or no reply in time. Calls that are not safe to
 * repeat (POST, e.g. reserving stock) are retried only when they certainly never
 * reached the service, so nothing is ever applied twice.
 */
public class WakeUpRetryInterceptor implements ClientHttpRequestInterceptor {
    
    private static final Logger log = LoggerFactory.getLogger(WakeUpRetryInterceptor.class);
    private static final long FIRST_DELAY_MS = 1_000;
    private static final long MAX_DELAY_MS = 10_000;
    
    private final Duration maxWait;
    private final LongSupplier nanoTime;
    private final Sleeper sleeper;
    
    public WakeUpRetryInterceptor(Duration maxWait) {
        this(maxWait, System::nanoTime, Thread::sleep);
    }
    
    WakeUpRetryInterceptor(Duration maxWait, LongSupplier nanoTime, Sleeper sleeper) {
        this.maxWait = maxWait;
        this.nanoTime = nanoTime;
        this.sleeper = sleeper;
    }
    
    private static boolean safeToRepeat(HttpMethod method) {
        return HttpMethod.GET.equals(method) || HttpMethod.HEAD.equals(method) || HttpMethod.DELETE.equals(method);
    }
    
    /**
     * Why the response suggests the service is still starting, or null if it doesn't.
     */
    private static String startingUp(ClientHttpResponse response, boolean safeToRepeat) throws IOException {
        MediaType type = response.getHeaders().getContentType();
        if (type != null && type.isCompatibleWith(MediaType.TEXT_HTML)) {
            return "HTML page instead of JSON";
        }
        int status = response.getStatusCode().value();
        if (safeToRepeat && (status == 502 || status == 503 || status == 504)) {
            return "HTTP " + status;
        }
        return null;
    }
    
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
        boolean safeToRepeat = safeToRepeat(request.getMethod());
        long deadline = nanoTime.getAsLong() + maxWait.toNanos();
        long delayMs = FIRST_DELAY_MS;
        while (true) {
            boolean timeLeft = nanoTime.getAsLong() + TimeUnit.MILLISECONDS.toNanos(delayMs) <= deadline;
            String reason;
            try {
                ClientHttpResponse response = execution.execute(request, body);
                reason = startingUp(response, safeToRepeat);
                if (reason == null || !timeLeft) {
                    return response;
                }
                response.close();
            } catch (IOException e) {
                // A refused connection never reached the service; a timeout may have.
                if (!timeLeft || !(e instanceof ConnectException || safeToRepeat)) {
                    throw e;
                }
                reason = e.getClass().getSimpleName();
            }
            log.info("{} {} not available yet ({}); retrying in {} ms",
                request.getMethod(), request.getURI(), reason, delayMs);
            try {
                sleeper.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new InterruptedIOException("Interrupted while waiting for " + request.getURI());
            }
            delayMs = Math.min(delayMs * 2, MAX_DELAY_MS);
        }
    }
    
    @FunctionalInterface
    interface Sleeper {
        void sleep(long millis) throws InterruptedException;
    }
}
