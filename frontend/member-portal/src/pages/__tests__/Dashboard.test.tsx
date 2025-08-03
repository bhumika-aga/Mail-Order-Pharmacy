import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { render, screen } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import { AuthProvider } from "../../hooks/useAuth";
import * as apiService from "../../services/api";
import Dashboard from "../Dashboard";

// Mock the API services
jest.mock("../../services/api", () => ({
  subscriptionService: {
    getMySubscriptions: jest.fn(),
  },
  refillService: {
    getAllRefillOrders: jest.fn(),
  },
}));

const mockedApiService = apiService as jest.Mocked<typeof apiService>;

// Mock the useAuth hook
jest.mock("../../hooks/useAuth", () => ({
  ...jest.requireActual("../../hooks/useAuth"),
  useAuth: () => ({
    user: {
      id: 1,
      username: "testuser",
      email: "test@example.com",
      memberId: "MEM123456",
      fullName: "Test User",
      createdAt: "2023-01-01",
      updatedAt: "2023-01-01",
    },
    logout: jest.fn(),
  }),
}));

describe("Dashboard Component", () => {
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
            <Dashboard />
          </BrowserRouter>
        </AuthProvider>
      </QueryClientProvider>
    );
  };

  beforeEach(() => {
    jest.clearAllMocks();

    // Default mock responses
    (mockedApiService.subscriptionService.getMySubscriptions as jest.MockedFunction<typeof mockedApiService.subscriptionService.getMySubscriptions>).mockResolvedValue(
      {
        data: [
          {
            subscriptionId: "SUB001",
            memberId: "MEM123456",
            subscriptionDate: "2023-01-01",
            prescriptionId: "PRESC001",
            refillFrequency: "MONTHLY",
            memberLocation: "New York",
            subscriptionStatus: "ACTIVE",
          },
          {
            subscriptionId: "SUB002",
            memberId: "MEM123456",
            subscriptionDate: "2023-01-02",
            prescriptionId: "PRESC002",
            refillFrequency: "WEEKLY",
            memberLocation: "New York",
            subscriptionStatus: "INACTIVE",
          },
        ],
        status: 200,
        statusText: "OK",
        headers: {},
        config: {} as any,
      }
    );

    (mockedApiService.refillService.getAllRefillOrders as jest.MockedFunction<typeof mockedApiService.refillService.getAllRefillOrders>).mockResolvedValue({
      data: [
        {
          refillOrderId: "ORDER001",
          memberId: "MEM123456",
          orderDate: "2023-01-01",
          memberLocation: "New York",
          orderStatus: "PENDING",
          orderType: "SCHEDULED",
          subscriptionId: "SUB001",
          deliveryDate: null,
          trackingNumber: null,
          lineItems: [],
          totalAmount: 99.99,
        },
        {
          refillOrderId: "ORDER002",
          memberId: "MEM123456",
          orderDate: "2023-01-02",
          memberLocation: "New York",
          orderStatus: "DELIVERED",
          orderType: "ADHOC",
          subscriptionId: null,
          deliveryDate: "2023-01-05",
          trackingNumber: "TRACK123",
          lineItems: [],
          totalAmount: 149.99,
        },
      ],
      status: 200,
      statusText: "OK",
      headers: {},
      config: {} as any,
    });
  });

  it("should render dashboard with welcome message", async () => {
    renderWithProviders();

    expect(screen.getByText("Welcome back, Test User!")).toBeInTheDocument();
    expect(
      screen.getByText("Here's an overview of your account activity")
    ).toBeInTheDocument();
  });

  it("should display dashboard cards with correct data", async () => {
    renderWithProviders();

    // Wait for data to load and check the cards
    expect(await screen.findByText("Active Subscriptions")).toBeInTheDocument();
    expect(await screen.findByText("Pending Refills")).toBeInTheDocument();
    expect(await screen.findByText("Total Orders")).toBeInTheDocument();

    // Check the values
    expect(screen.getByText("1")).toBeInTheDocument(); // Active subscriptions
    expect(screen.getByText("1")).toBeInTheDocument(); // Pending refills
    expect(screen.getByText("2")).toBeInTheDocument(); // Total orders
  });

  it("should handle loading state", () => {
    // Mock pending promises
    mockedApiService.subscriptionService.getMemberSubscriptions.mockReturnValue(
      new Promise(() => {}) // Never resolves
    );
    mockedApiService.refillService.getRefillOrders.mockReturnValue(
      new Promise(() => {}) // Never resolves
    );

    renderWithProviders();

    expect(screen.getByText("Welcome back, Test User!")).toBeInTheDocument();
    // The cards should still render with default values
    expect(screen.getByText("Active Subscriptions")).toBeInTheDocument();
  });

  it("should handle API errors gracefully", async () => {
    mockedApiService.subscriptionService.getMemberSubscriptions.mockRejectedValue(
      new Error("Failed to fetch subscriptions")
    );
    mockedApiService.refillService.getRefillOrders.mockRejectedValue(
      new Error("Failed to fetch refills")
    );

    renderWithProviders();

    // The component should still render even if API calls fail
    expect(screen.getByText("Welcome back, Test User!")).toBeInTheDocument();
    expect(screen.getByText("Active Subscriptions")).toBeInTheDocument();
    expect(screen.getByText("Pending Refills")).toBeInTheDocument();
    expect(screen.getByText("Total Orders")).toBeInTheDocument();
  });

  it("should calculate correct counts for different subscription statuses", async () => {
    renderWithProviders();

    // Should show 1 active subscription (out of 2 total)
    expect(await screen.findByText("1")).toBeInTheDocument();
  });

  it("should calculate correct counts for different order statuses", async () => {
    renderWithProviders();

    // Should show 1 pending refill (out of 2 total orders)
    expect(await screen.findByText("1")).toBeInTheDocument();
    // Should show 2 total orders
    expect(await screen.findByText("2")).toBeInTheDocument();
  });

  it("should handle empty data arrays", async () => {
    (mockedApiService.subscriptionService.getMySubscriptions as jest.MockedFunction<typeof mockedApiService.subscriptionService.getMySubscriptions>).mockResolvedValue(
      {
        data: [],
        status: 200,
        statusText: "OK",
        headers: {},
        config: {} as any,
      }
    );
    (mockedApiService.refillService.getAllRefillOrders as jest.MockedFunction<typeof mockedApiService.refillService.getAllRefillOrders>).mockResolvedValue({
      data: [],
      status: 200,
      statusText: "OK",
      headers: {},
      config: {} as any,
    });

    renderWithProviders();

    // Should show 0 for all counts
    expect(await screen.findByText("0")).toBeInTheDocument();
  });

  it("should render navigation cards", () => {
    renderWithProviders();

    expect(screen.getByText("Quick Actions")).toBeInTheDocument();
    expect(screen.getByText("Manage Drugs")).toBeInTheDocument();
    expect(screen.getByText("View Subscriptions")).toBeInTheDocument();
    expect(screen.getByText("Track Refills")).toBeInTheDocument();
  });

  it("should have correct navigation links", () => {
    renderWithProviders();

    const drugsLink = screen.getByText("Manage Drugs").closest("a");
    const subscriptionsLink = screen
      .getByText("View Subscriptions")
      .closest("a");
    const refillsLink = screen.getByText("Track Refills").closest("a");

    expect(drugsLink).toHaveAttribute("href", "/drugs");
    expect(subscriptionsLink).toHaveAttribute("href", "/subscriptions");
    expect(refillsLink).toHaveAttribute("href", "/refills");
  });
});
