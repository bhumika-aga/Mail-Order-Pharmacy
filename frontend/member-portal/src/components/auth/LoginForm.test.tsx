import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import type { AuthContextType } from "../../contexts/authContext";
import { ThemeProvider } from "../../contexts/ThemeContext";
import { useAuth } from "../../hooks/useAuth";

vi.mock("../../hooks/useAuth", () => ({ useAuth: vi.fn() }));

/** The setting is read when the module loads, so load it fresh after stubbing env. */
const renderLogin = async () => {
  vi.resetModules();
  const { default: LoginForm } = await import("./LoginForm");
  render(
    <ThemeProvider>
      <MemoryRouter>
        <LoginForm />
      </MemoryRouter>
    </ThemeProvider>,
  );
};

describe("LoginForm demo credentials", () => {
  beforeEach(() => {
    vi.mocked(useAuth).mockReturnValue({
      login: vi.fn(),
    } as unknown as AuthContextType);
  });
  afterEach(() => vi.unstubAllEnvs());

  it("are hidden in a production build: those accounts don't exist there", async () => {
    vi.stubEnv("DEV", false);
    vi.stubEnv("VITE_SHOW_DEMO_CREDENTIALS", "");
    await renderLogin();

    expect(screen.getByRole("button", { name: "Sign In" })).toBeInTheDocument();
    expect(screen.queryByText(/Demo Credentials/)).not.toBeInTheDocument();
    expect(screen.queryByText(/member1/)).not.toBeInTheDocument();
  });

  it("are shown in a build that opts in (the docker-compose stack)", async () => {
    vi.stubEnv("DEV", false);
    vi.stubEnv("VITE_SHOW_DEMO_CREDENTIALS", "true");
    await renderLogin();

    expect(screen.getByText(/Demo Credentials/)).toBeInTheDocument();
  });

  it("are shown on the local dev server", async () => {
    vi.stubEnv("DEV", true);
    await renderLogin();

    expect(screen.getByText(/Demo Credentials/)).toBeInTheDocument();
  });
});
