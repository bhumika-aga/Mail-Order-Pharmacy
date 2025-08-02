import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import * as apiService from "../../services/api";
import SignupPage from "../SignupPage";

// Mock the API service
jest.mock("../../services/api", () => ({
  authService: {
    register: jest.fn(),
    checkUsernameAvailability: jest.fn(),
    checkEmailAvailability: jest.fn(),
  },
}));

const mockedApiService = apiService as jest.Mocked<typeof apiService>;

// Mock react-router-dom
const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockNavigate,
}));

describe("SignupPage Component", () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });

  const renderWithProviders = () => {
    return render(
      <QueryClientProvider client={queryClient}>
        <BrowserRouter>
          <SignupPage />
        </BrowserRouter>
      </QueryClientProvider>
    );
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("should render signup form", () => {
    renderWithProviders();

    expect(screen.getByText("Create Account")).toBeInTheDocument();
    expect(screen.getByLabelText(/username/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/full name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: /create account/i })
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
    mockedApiService.authService.register.mockResolvedValue({
      data: { message: "User registered successfully!" },
    });

    renderWithProviders();

    fireEvent.change(screen.getByLabelText(/username/i), {
      target: { value: "testuser" },
    });
    fireEvent.change(screen.getByLabelText(/full name/i), {
      target: { value: "Test User" },
    });
    fireEvent.change(screen.getByLabelText(/email/i), {
      target: { value: "test@example.com" },
    });
    fireEvent.change(screen.getByLabelText(/password/i), {
      target: { value: "password123" },
    });

    fireEvent.click(screen.getByRole("button", { name: /create account/i }));

    await waitFor(() => {
      expect(mockedApiService.authService.register).toHaveBeenCalledWith({
        username: "testuser",
        fullName: "Test User",
        email: "test@example.com",
        password: "password123",
      });
    });
  });

  it("should check username availability on blur", async () => {
    mockedApiService.authService.checkUsernameAvailability.mockResolvedValue({
      data: { message: "Username is available" },
    });

    renderWithProviders();

    const usernameField = screen.getByLabelText(/username/i);
    fireEvent.change(usernameField, { target: { value: "testuser" } });
    fireEvent.blur(usernameField);

    await waitFor(() => {
      expect(
        mockedApiService.authService.checkUsernameAvailability
      ).toHaveBeenCalledWith("testuser");
    });
  });

  it('should check email availability and show "Email is valid" message', async () => {
    mockedApiService.authService.checkEmailAvailability.mockResolvedValue({
      data: { message: "Email is available" },
    });

    renderWithProviders();

    const emailField = screen.getByLabelText(/email/i);
    fireEvent.change(emailField, { target: { value: "test@example.com" } });
    fireEvent.blur(emailField);

    await waitFor(() => {
      expect(
        mockedApiService.authService.checkEmailAvailability
      ).toHaveBeenCalledWith("test@example.com");
    });

    await waitFor(() => {
      expect(screen.getByText("Email is valid")).toBeInTheDocument();
    });
  });

  it("should show unavailable username message", async () => {
    mockedApiService.authService.checkUsernameAvailability.mockResolvedValue({
      data: { message: "Username is already taken" },
    });

    renderWithProviders();

    const usernameField = screen.getByLabelText(/username/i);
    fireEvent.change(usernameField, { target: { value: "existinguser" } });
    fireEvent.blur(usernameField);

    await waitFor(() => {
      expect(screen.getByText("Username is already taken")).toBeInTheDocument();
    });
  });

  it("should navigate to login on successful registration", async () => {
    mockedApiService.authService.register.mockResolvedValue({
      data: { message: "User registered successfully!" },
    });

    renderWithProviders();

    fireEvent.change(screen.getByLabelText(/username/i), {
      target: { value: "testuser" },
    });
    fireEvent.change(screen.getByLabelText(/full name/i), {
      target: { value: "Test User" },
    });
    fireEvent.change(screen.getByLabelText(/email/i), {
      target: { value: "test@example.com" },
    });
    fireEvent.change(screen.getByLabelText(/password/i), {
      target: { value: "password123" },
    });

    fireEvent.click(screen.getByRole("button", { name: /create account/i }));

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith("/login", {
        state: {
          message:
            "Registration successful! Please log in with your credentials.",
        },
      });
    });
  });

  it("should show error message on registration failure", async () => {
    mockedApiService.authService.register.mockRejectedValue(
      new Error("Registration failed")
    );

    renderWithProviders();

    fireEvent.change(screen.getByLabelText(/username/i), {
      target: { value: "testuser" },
    });
    fireEvent.change(screen.getByLabelText(/full name/i), {
      target: { value: "Test User" },
    });
    fireEvent.change(screen.getByLabelText(/email/i), {
      target: { value: "test@example.com" },
    });
    fireEvent.change(screen.getByLabelText(/password/i), {
      target: { value: "password123" },
    });

    fireEvent.click(screen.getByRole("button", { name: /create account/i }));

    await waitFor(() => {
      expect(
        screen.getByText("Registration failed. Please try again.")
      ).toBeInTheDocument();
    });
  });

  it("should show link to login page", () => {
    renderWithProviders();

    const loginLink = screen.getByText(/sign in here/i);
    expect(loginLink).toBeInTheDocument();
    expect(loginLink.closest("a")).toHaveAttribute("href", "/login");
  });

  it("should not submit form with empty required fields", () => {
    renderWithProviders();

    fireEvent.click(screen.getByRole("button", { name: /create account/i }));

    expect(mockedApiService.authService.register).not.toHaveBeenCalled();
  });

  it("should clear validation messages when user starts typing", async () => {
    mockedApiService.authService.checkUsernameAvailability.mockResolvedValue({
      data: { message: "Username is already taken" },
    });

    renderWithProviders();

    const usernameField = screen.getByLabelText(/username/i);
    fireEvent.change(usernameField, { target: { value: "existinguser" } });
    fireEvent.blur(usernameField);

    await waitFor(() => {
      expect(screen.getByText("Username is already taken")).toBeInTheDocument();
    });

    // Start typing again
    fireEvent.change(usernameField, { target: { value: "existinguser2" } });

    expect(
      screen.queryByText("Username is already taken")
    ).not.toBeInTheDocument();
  });

  it("should disable submit button when mutation is pending", async () => {
    mockedApiService.authService.register.mockImplementation(
      () => new Promise((resolve) => setTimeout(resolve, 1000))
    );

    renderWithProviders();

    fireEvent.change(screen.getByLabelText(/username/i), {
      target: { value: "testuser" },
    });
    fireEvent.change(screen.getByLabelText(/full name/i), {
      target: { value: "Test User" },
    });
    fireEvent.change(screen.getByLabelText(/email/i), {
      target: { value: "test@example.com" },
    });
    fireEvent.change(screen.getByLabelText(/password/i), {
      target: { value: "password123" },
    });

    fireEvent.click(screen.getByRole("button", { name: /create account/i }));

    await waitFor(() => {
      expect(
        screen.getByRole("button", { name: /creating account/i })
      ).toBeDisabled();
    });
  });
});
