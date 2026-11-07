import { isAxiosError } from "axios";

/**
 * A user-facing message for a failed API call. The backend services return
 * `{ message }` and/or `{ error }`; fall back to a generic text otherwise.
 */
export const apiErrorMessage = (error: unknown, fallback: string): string => {
  if (isAxiosError(error)) {
    const data = error.response?.data as
      | { message?: unknown; error?: unknown }
      | undefined;
    if (typeof data?.message === "string" && data.message) return data.message;
    if (typeof data?.error === "string" && data.error) return data.error;
  }
  return fallback;
};
