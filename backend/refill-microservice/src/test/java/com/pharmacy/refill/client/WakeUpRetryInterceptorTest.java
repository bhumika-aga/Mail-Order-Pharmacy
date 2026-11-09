package com.pharmacy.refill.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WakeUpRetryInterceptorTest {
    
    /**
     * A fake clock: sleeping advances it instantly.
     */
    private long now;
    private int calls;
    
    private static ClientHttpResponse json(HttpStatus status) {
        MockClientHttpResponse response = new MockClientHttpResponse("{}".getBytes(), status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response;
    }
    
    private static ClientHttpResponse loadingPage() {
        MockClientHttpResponse response = new MockClientHttpResponse("<html>waking up</html>".getBytes(), HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.TEXT_HTML);
        return response;
    }
    
    private static MockClientHttpRequest request(HttpMethod method) {
        return new MockClientHttpRequest(method, URI.create("https://drugs.example.com/api/drugs/x"));
    }
    
    private WakeUpRetryInterceptor interceptor(Duration maxWait) {
        return new WakeUpRetryInterceptor(maxWait, () -> now, millis -> now += TimeUnit.MILLISECONDS.toNanos(millis));
    }
    
    /**
     * Plays back the given outcomes: a response, or an IOException to throw.
     */
    private ClientHttpRequestExecution outcomes(Object... outcomes) {
        Deque<Object> queue = new ArrayDeque<>(java.util.List.of(outcomes));
        return (request, body) -> {
            calls++;
            Object next = queue.size() > 1 ? queue.poll() : queue.peek();
            if (next instanceof IOException e) {
                throw e;
            }
            return (ClientHttpResponse) next;
        };
    }
    
    @Test
    void waitsForAServiceThatIsWakingUp() throws IOException {
        ClientHttpResponse response = interceptor(Duration.ofSeconds(90)).intercept(request(HttpMethod.GET), new byte[0],
            outcomes(new ConnectException("refused"), loadingPage(), json(HttpStatus.SERVICE_UNAVAILABLE),
                new SocketTimeoutException("read timed out"), json(HttpStatus.OK)));
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(calls).isEqualTo(5);
    }
    
    @Test
    void givesUpWhenTheWaitIsOver() {
        assertThatThrownBy(() -> interceptor(Duration.ofSeconds(20)).intercept(request(HttpMethod.GET), new byte[0],
            outcomes(new ConnectException("refused"))))
            .isInstanceOf(ConnectException.class);
        assertThat(now).isLessThanOrEqualTo(TimeUnit.SECONDS.toNanos(20));
        assertThat(calls).isGreaterThan(1);
    }
    
    @Test
    void neverRepeatsAPostThatMayHaveReachedTheService() {
        // e.g. reserving stock: a timed-out reply may mean it was applied
        assertThatThrownBy(() -> interceptor(Duration.ofSeconds(90)).intercept(request(HttpMethod.POST), new byte[0],
            outcomes(new SocketTimeoutException("read timed out"), json(HttpStatus.OK))))
            .isInstanceOf(SocketTimeoutException.class);
        assertThat(calls).isEqualTo(1);
    }
    
    @Test
    void repeatsAPostOnlyWhenItCertainlyNeverArrived() throws IOException {
        ClientHttpResponse response = interceptor(Duration.ofSeconds(90)).intercept(request(HttpMethod.POST), new byte[0],
            outcomes(new ConnectException("refused"), loadingPage(), json(HttpStatus.OK)));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(calls).isEqualTo(3);
        
        calls = 0;
        // a 503 to a POST is returned as it is: the service may have processed it
        response = interceptor(Duration.ofSeconds(90)).intercept(request(HttpMethod.POST), new byte[0],
            outcomes(json(HttpStatus.SERVICE_UNAVAILABLE), json(HttpStatus.OK)));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(calls).isEqualTo(1);
    }
    
    @Test
    void ordinaryErrorsAreReturnedAtOnce() throws IOException {
        ClientHttpResponse response = interceptor(Duration.ofSeconds(90)).intercept(request(HttpMethod.GET), new byte[0],
            outcomes(json(HttpStatus.NOT_FOUND)));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(calls).isEqualTo(1);
    }
}
