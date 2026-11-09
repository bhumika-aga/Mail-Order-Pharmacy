/**
 * Backend service base URLs. Production builds set the VITE_* variables (see
 * .env.example); unset ones fall back to the local dev ports.
 *
 * Shared by the app (API clients) and the build (vite.config.ts derives the
 * Content-Security-Policy's allowed API origins from the same values), so the
 * two can never disagree. Keep this file free of browser-only APIs.
 */
export const DEFAULT_SERVICE_URLS = {
  VITE_AUTH_SERVICE_URL: "http://localhost:8084",
  VITE_DRUGS_SERVICE_URL: "http://localhost:8081",
  VITE_SUBSCRIPTION_SERVICE_URL: "http://localhost:8082",
  VITE_REFILL_SERVICE_URL: "http://localhost:8083",
} as const;

export type ServiceUrlKey = keyof typeof DEFAULT_SERVICE_URLS;

export const SERVICE_URL_KEYS = Object.keys(
  DEFAULT_SERVICE_URLS,
) as ServiceUrlKey[];

export const resolveServiceUrl = (
  env: Record<string, unknown>,
  key: ServiceUrlKey,
): string => {
  const value = env[key];
  return typeof value === "string" && value.trim()
    ? value.trim()
    : DEFAULT_SERVICE_URLS[key];
};
