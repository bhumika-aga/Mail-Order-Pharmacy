import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import { AuthProvider } from "../../../hooks/useAuth";
import LoginForm from "../LoginForm";

// Mock the useAuth hook
const mockLogin = jest.fn();
const mockUseAuth = {
  user: null,
  login: mockLogin,
  logout: jest.fn(),
  isLoading: false,
};

jest.mock("../../../hooks/useAuth", () => ({
  ...jest.requireActual("../../../hooks/useAuth"),
  useAuth: () => mockUseAuth,
}));

// Mock react-router-dom
const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockNavigate,
}));

describe("LoginForm Component", () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });

  const renderWithProviders = () => {
    return render(
      <QueryClientProvider client={queryClient}>
        <AuthProvider>
          <BrowserRouter>
            <LoginForm />
          </BrowserRouter>
        </AuthProvider>
      </QueryClientProvider>
    );
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("should render login form", () => {
    renderWithProviders();

    expect(screen.getByText("Welcome Back")).toBeInTheDocument();
    expect(screen.getByLabelText(/username/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: /sign in/i })
    ).toBeInTheDocument();
  });

  it("should show password visibility toggle", () => {
    renderWithProviders();

    const passwordField = screen.getByLabelText(/password/i);
    expect(passwordField).toHaveAttribute("type", "password");

    const toggleButton = screen.getByLabelText(/toggle password visibility/i);
    fireEvent.click(toggleButton);

    expect(passwordField).toHaveAttribute("type", "text");
  });

  it("should handle form submission with valid data", async () => {
    mockLogin.mockResolvedValue({});

    renderWithProviders();

    const usernameField = screen.getByLabelText(/username/i);
    const passwordField = screen.getByLabelText(/password/i);
    const submitButton = screen.getByRole("button", { name: /sign in/i });

    fireEvent.change(usernameField, { target: { value: "testuser" } });
    fireEvent.change(passwordField, { target: { value: "password123" } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalledWith("testuser", "password123");
    });
  });

  it("should show validation errors for empty fields", async () => {
    renderWithProviders();

    const submitButton = screen.getByRole("button", { name: /sign in/i });
    fireEvent.click(submitButton);

    // The form should not submit without required fields
    expect(mockLogin).not.toHaveBeenCalled();
  });

  it("should handle login error", async () => {
    const loginError = new Error("Invalid credentials");
    mockLogin.mockRejectedValue(loginError);

    renderWithProviders();

    const usernameField = screen.getByLabelText(/username/i);
    const passwordField = screen.getByLabelText(/password/i);
    const submitButton = screen.getByRole("button", { name: /sign in/i });

    fireEvent.change(usernameField, { target: { value: "testuser" } });
    fireEvent.change(passwordField, { target: { value: "wrongpassword" } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalledWith("testuser", "wrongpassword");
    });
  });

  it("should navigate to dashboard on successful login", async () => {
    mockLogin.mockResolvedValue({});

    renderWithProviders();

    const usernameField = screen.getByLabelText(/username/i);
    const passwordField = screen.getByLabelText(/password/i);
    const submitButton = screen.getByRole("button", { name: /sign in/i });

    fireEvent.change(usernameField, { target: { value: "testuser" } });
    fireEvent.change(passwordField, { target: { value: "password123" } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith("/dashboard");
    });
  });

  it("should show link to signup page", () => {
    renderWithProviders();

    const signupLink = screen.getByText(/create an account/i);
    expect(signupLink).toBeInTheDocument();
    expect(signupLink.closest("a")).toHaveAttribute("href", "/signup");
  });

  it("should disable submit button when loading", () => {
    mockUseAuth.isLoading = true;

    renderWithProviders();

    const submitButton = screen.getByRole("button", { name: /signing in/i });
    expect(submitButton).toBeDisabled();

    // Reset for other tests
    mockUseAuth.isLoading = false;
  });
});
