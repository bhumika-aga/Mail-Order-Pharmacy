/// <reference types="vitest/config" />
import react from "@vitejs/plugin-react";
import { fileURLToPath } from "node:url";
import { defineConfig, loadEnv, type Plugin } from "vite";
import { resolveServiceUrl, SERVICE_URL_KEYS } from "./src/config/serviceUrls";

/**
 * Adds a Content-Security-Policy <meta> tag to index.html in production builds.
 * It works on any static host, and its allowed API origins come from the same
 * VITE_* service URLs the app calls, so the policy always matches the deployment.
 *
 * - scripts: only the app's own bundle (injected <script>s and inline JS are blocked)
 * - connect: only the app's origin and the configured backend services, so an
 *   injected script cannot send the session token anywhere else
 * - styles: 'unsafe-inline' is required by MUI/Emotion's runtime <style> tags
 *
 * Not applied to the dev server, whose hot reload relies on inline scripts.
 * (frame-ancestors can't be set from a <meta> tag; nginx.conf sends X-Frame-Options.)
 */
function contentSecurityPolicy(env: Record<string, string>): Plugin {
  return {
    name: "mediflow-content-security-policy",
    apply: "build",
    transformIndexHtml() {
      const apiOrigins = [
        ...new Set(
          SERVICE_URL_KEYS.map((key) => {
            const url = resolveServiceUrl(env, key);
            try {
              return new URL(url).origin;
            } catch {
              throw new Error(`${key} is not a valid URL: "${url}"`);
            }
          }),
        ),
      ];
      const policy = [
        "default-src 'self'",
        "script-src 'self'",
        "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com",
        "font-src 'self' https://fonts.gstatic.com",
        "img-src 'self' data:",
        `connect-src 'self' ${apiOrigins.join(" ")}`,
        "object-src 'none'",
        "base-uri 'self'",
        "form-action 'self'",
      ].join("; ");
      return [
        {
          tag: "meta",
          attrs: { "http-equiv": "Content-Security-Policy", content: policy },
          injectTo: "head-prepend",
        },
      ];
    },
  };
}

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  // VITE_* from .env files and the environment (CI / docker build args)
  const env = loadEnv(mode, process.cwd(), "VITE_");
  return {
    plugins: [react(), contentSecurityPolicy(env)],
    resolve: {
      alias: {
        "@": fileURLToPath(new URL("./src", import.meta.url)),
      },
    },
    build: {
      rolldownOptions: {
        output: {
          // Long-lived framework code gets its own cacheable chunks. Libraries used only
          // by lazy-loaded pages (date pickers, date-fns) stay with those pages.
          codeSplitting: {
            groups: [
              {
                name: "react",
                test: /node_modules[\\/](react|react-dom|react-router|react-router-dom|scheduler)[\\/]/,
                priority: 20,
              },
              {
                name: "mui",
                test: /node_modules[\\/](@mui[\\/](material|system|styled-engine|utils|private-theming)|@emotion|@popperjs)[\\/]/,
                priority: 10,
              },
            ],
          },
        },
      },
    },
    test: {
      environment: "jsdom",
      setupFiles: ["./src/test/setup.ts"],
      css: false,
    },
  };
});
