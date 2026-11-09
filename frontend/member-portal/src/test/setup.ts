// Adds the jest-dom matchers (toBeInTheDocument, ...) to Vitest's expect
import "@testing-library/jest-dom/vitest";
import { cleanup } from "@testing-library/react";
import { afterEach } from "vitest";

// Testing Library only unmounts automatically when Vitest globals are enabled
// (they aren't here), so remove each test's rendered output explicitly.
afterEach(() => cleanup());
