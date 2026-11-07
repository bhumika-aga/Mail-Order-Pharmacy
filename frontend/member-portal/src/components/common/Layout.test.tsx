import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";
import type { AuthContextType } from "../../contexts/authContext";
import { ThemeProvider } from "../../contexts/ThemeContext";
import { useAuth } from "../../hooks/useAuth";
import Layout from "./Layout";

vi.mock("../../hooks/useAuth", () => ({ useAuth: vi.fn() }));

const authState = (isAdmin: boolean): AuthContextType => ({
  user: {
    id: 0,
    username: isAdmin ? "admin" : "member1",
    email: "",
    memberId: isAdmin ? "" : "MEM001",
    fullName: "",
    role: isAdmin ? "ADMIN" : "MEMBER",
    createdAt: "",
    updatedAt: "",
  },
  login: vi.fn(),
  logout: vi.fn(),
  loading: false,
  isAuthenticated: true,
  isAdmin,
});

const renderLayout = () =>
  render(
    <ThemeProvider>
      <MemoryRouter initialEntries={["/dashboard"]}>
        <Layout>
          <p>page content</p>
        </Layout>
      </MemoryRouter>
    </ThemeProvider>,
  );

describe("Layout navigation", () => {
  beforeEach(() => vi.mocked(useAuth).mockReset());

  it("hides the admin-only Users link from members", () => {
    vi.mocked(useAuth).mockReturnValue(authState(false));
    renderLayout();

    expect(screen.getByRole("button", { name: "Refills" })).toBeInTheDocument();
    expect(
      screen.queryByRole("button", { name: "Users" }),
    ).not.toBeInTheDocument();
    expect(screen.getByText("page content")).toBeInTheDocument();
  });

  it("shows the Users link to admins", () => {
    vi.mocked(useAuth).mockReturnValue(authState(true));
    renderLayout();

    expect(screen.getByRole("button", { name: "Users" })).toBeInTheDocument();
  });
});
