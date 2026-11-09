/// <reference types="vite/client" />

/** Build-time configuration (see .env.example); all optional, with local defaults. */
interface ImportMetaEnv {
  readonly VITE_AUTH_SERVICE_URL?: string;
  readonly VITE_DRUGS_SERVICE_URL?: string;
  readonly VITE_SUBSCRIPTION_SERVICE_URL?: string;
  readonly VITE_REFILL_SERVICE_URL?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
