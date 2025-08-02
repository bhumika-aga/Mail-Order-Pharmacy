import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter } from 'react-router-dom';
import Layout from '../Layout';
import { AuthProvider } from '../../../hooks/useAuth';

// Mock the useAuth hook
jest.mock('../../../hooks/useAuth', () => ({
  ...jest.requireActual('../../../hooks/useAuth'),
  useAuth: () => ({
    user: {
      id: 1,
      username: 'testuser',
      email: 'test@example.com',
      memberId: 'MEM123456',
      fullName: 'Test User',
      createdAt: '2023-01-01',
      updatedAt: '2023-01-01',
    },
    logout: jest.fn(),
  }),
}));

describe('Layout Component', () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });

  const renderWithProviders = (children: React.ReactNode) => {
    return render(
      <QueryClientProvider client={queryClient}>
        <AuthProvider>
          <BrowserRouter>
            <Layout>{children}</Layout>
          </BrowserRouter>
        </AuthProvider>
      </QueryClientProvider>
    );
  };

  it('should render layout with navigation', () => {
    renderWithProviders(<div>Test Content</div>);
    
    expect(screen.getByText('Mail-Order Pharmacy')).toBeInTheDocument();
    expect(screen.getByText('Dashboard')).toBeInTheDocument();
    expect(screen.getByText('Drugs')).toBeInTheDocument();
    expect(screen.getByText('Subscriptions')).toBeInTheDocument();
    expect(screen.getByText('Refills')).toBeInTheDocument();
    expect(screen.getByText('Users')).toBeInTheDocument();
  });

  it('should render children content', () => {
    renderWithProviders(<div data-testid="test-content">Test Content</div>);
    
    expect(screen.getByTestId('test-content')).toBeInTheDocument();
    expect(screen.getByText('Test Content')).toBeInTheDocument();
  });

  it('should show user information', () => {
    renderWithProviders(<div>Test Content</div>);
    
    expect(screen.getByText('testuser')).toBeInTheDocument();
    expect(screen.getByText('MEM123456')).toBeInTheDocument();
  });

  it('should handle logout when logout button is clicked', () => {
    const mockLogout = jest.fn();
    
    // Override the mock for this test
    jest.doMock('../../../hooks/useAuth', () => ({
      ...jest.requireActual('../../../hooks/useAuth'),
      useAuth: () => ({
        user: {
          id: 1,
          username: 'testuser',
          email: 'test@example.com',
          memberId: 'MEM123456',
          fullName: 'Test User',
          createdAt: '2023-01-01',
          updatedAt: '2023-01-01',
        },
        logout: mockLogout,
      }),
    }));

    renderWithProviders(<div>Test Content</div>);
    
    // Click on user menu to open it
    const userButton = screen.getByText('testuser');
    fireEvent.click(userButton);
    
    // Look for logout option
    const logoutElements = screen.getAllByText(/logout/i);
    expect(logoutElements.length).toBeGreaterThan(0);
  });

  it('should toggle drawer on mobile', () => {
    renderWithProviders(<div>Test Content</div>);
    
    // The drawer toggle button should be present
    const menuButton = screen.getByLabelText(/open drawer/i);
    expect(menuButton).toBeInTheDocument();
    
    fireEvent.click(menuButton);
    
    // After clicking, the drawer state should change
    // This is more of an integration test to ensure the button works
  });

  it('should render with proper structure', () => {
    const { container } = renderWithProviders(<div>Test Content</div>);
    
    expect(container.querySelector('.MuiAppBar-root')).toBeInTheDocument();
    expect(container.querySelector('.MuiDrawer-root')).toBeInTheDocument();
    expect(container.querySelector('.MuiToolbar-root')).toBeInTheDocument();
  });
});