import React from 'react';
import { renderHook, act } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useAuth, AuthProvider } from '../useAuth';
import * as apiService from '../../services/api';

// Mock the API service
jest.mock('../../services/api', () => ({
  authService: {
    login: jest.fn(),
    validateToken: jest.fn(),
  },
}));

const mockedApiService = apiService as jest.Mocked<typeof apiService>;

describe('useAuth Hook', () => {
  let queryClient: QueryClient;

  const createWrapper = () => {
    queryClient = new QueryClient({
      defaultOptions: {
        queries: { retry: false },
        mutations: { retry: false },
      },
    });

    return ({ children }: { children: React.ReactNode }) => (
      <QueryClientProvider client={queryClient}>
        <AuthProvider>{children}</AuthProvider>
      </QueryClientProvider>
    );
  };

  beforeEach(() => {
    jest.clearAllMocks();
    // Mock localStorage
    Object.defineProperty(window, 'localStorage', {
      value: {
        getItem: jest.fn(),
        setItem: jest.fn(),
        removeItem: jest.fn(),
        clear: jest.fn(),
      },
      writable: true,
    });
  });

  it('should initialize with no user and not loading', () => {
    const wrapper = createWrapper();
    const { result } = renderHook(() => useAuth(), { wrapper });

    expect(result.current.user).toBeNull();
    expect(result.current.isLoading).toBe(false);
  });

  it('should login successfully', async () => {
    const mockAuthResponse = {
      data: {
        token: 'jwt-token',
        username: 'testuser',
        memberId: 'MEM123456',
        expirationTime: 900000,
      },
    };

    mockedApiService.authService.login.mockResolvedValue(mockAuthResponse);
    
    const wrapper = createWrapper();
    const { result } = renderHook(() => useAuth(), { wrapper });

    await act(async () => {
      await result.current.login('testuser', 'password');
    });

    expect(mockedApiService.authService.login).toHaveBeenCalledWith('testuser', 'password');
    expect(result.current.user).toEqual({
      id: 0,
      username: 'testuser',
      email: '',
      memberId: 'MEM123456',
      fullName: '',
      createdAt: '',
      updatedAt: '',
    });
  });

  it('should handle login error', async () => {
    const loginError = new Error('Invalid credentials');
    mockedApiService.authService.login.mockRejectedValue(loginError);
    
    const wrapper = createWrapper();
    const { result } = renderHook(() => useAuth(), { wrapper });

    await act(async () => {
      try {
        await result.current.login('testuser', 'wrongpassword');
      } catch (error) {
        expect(error).toBe(loginError);
      }
    });

    expect(result.current.user).toBeNull();
  });

  it('should logout successfully', async () => {
    const mockAuthResponse = {
      data: {
        token: 'jwt-token',
        username: 'testuser',
        memberId: 'MEM123456',
        expirationTime: 900000,
      },
    };

    mockedApiService.authService.login.mockResolvedValue(mockAuthResponse);
    
    const wrapper = createWrapper();
    const { result } = renderHook(() => useAuth(), { wrapper });

    // First login
    await act(async () => {
      await result.current.login('testuser', 'password');
    });

    expect(result.current.user).not.toBeNull();

    // Then logout
    act(() => {
      result.current.logout();
    });

    expect(result.current.user).toBeNull();
    expect(localStorage.removeItem).toHaveBeenCalledWith('token');
  });

  it('should validate token on mount when token exists', async () => {
    const mockToken = 'existing-jwt-token';
    const mockValidateResponse = {
      data: {
        token: mockToken,
        username: 'testuser',
        memberId: 'MEM123456',
        expirationTime: 900000,
      },
    };

    (localStorage.getItem as jest.Mock).mockReturnValue(mockToken);
    mockedApiService.authService.validateToken.mockResolvedValue(mockValidateResponse);
    
    const wrapper = createWrapper();
    const { result } = renderHook(() => useAuth(), { wrapper });

    // Wait for the validation to complete
    await act(async () => {
      await new Promise(resolve => setTimeout(resolve, 0));
    });

    expect(mockedApiService.authService.validateToken).toHaveBeenCalledWith(mockToken);
    expect(result.current.user).toEqual({
      id: 0,
      username: 'testuser',
      email: '',
      memberId: 'MEM123456',
      fullName: '',
      createdAt: '',
      updatedAt: '',
    });
  });

  it('should handle token validation failure', async () => {
    const mockToken = 'invalid-jwt-token';
    
    (localStorage.getItem as jest.Mock).mockReturnValue(mockToken);
    mockedApiService.authService.validateToken.mockRejectedValue(new Error('Invalid token'));
    
    const wrapper = createWrapper();
    const { result } = renderHook(() => useAuth(), { wrapper });

    // Wait for the validation to complete
    await act(async () => {
      await new Promise(resolve => setTimeout(resolve, 0));
    });

    expect(result.current.user).toBeNull();
    expect(localStorage.removeItem).toHaveBeenCalledWith('token');
  });
});