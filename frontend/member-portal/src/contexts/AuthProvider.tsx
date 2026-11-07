import { ReactNode, useCallback, useEffect, useState } from "react";
import {
  authService,
  clearAuthToken,
  setAuthToken,
  setUnauthorizedHandler,
} from "../services/api";
import { AuthResponse, User } from "../types";
import { AuthContext } from "./authContext";

const TOKEN_KEY = "token";

// The auth API returns only identity fields; profile details are not needed here.
const toUser = (authData: AuthResponse): User => ({
  id: 0,
  username: authData.username,
  email: "",
  memberId: authData.memberId,
  fullName: "",
  role: authData.role,
  createdAt: "",
  updatedAt: "",
});

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  // Only show the loading state while a stored token is being validated.
  const [loading, setLoading] = useState(
    () => localStorage.getItem(TOKEN_KEY) !== null,
  );

  const logout = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY);
    setUser(null);
    clearAuthToken();
  }, []);

  // Restore the session from a stored token on first load.
  useEffect(() => {
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token) {
      return;
    }
    let cancelled = false;
    authService
      .validateToken(token)
      .then((response) => {
        if (cancelled) return;
        setAuthToken(token);
        setUser(toUser(response.data));
      })
      .catch(() => {
        localStorage.removeItem(TOKEN_KEY);
        clearAuthToken();
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, []);

  // An expired or revoked token makes any API call return 401: log out so the
  // protected routes send the user back to the login page.
  useEffect(() => {
    setUnauthorizedHandler(logout);
    return () => setUnauthorizedHandler(null);
  }, [logout]);

  const login = async (username: string, password: string) => {
    try {
      const response = await authService.login(username, password);
      localStorage.setItem(TOKEN_KEY, response.data.token);
      setAuthToken(response.data.token);
      setUser(toUser(response.data));
    } catch (error) {
      throw new Error("Login failed", { cause: error });
    }
  };

  const value = {
    user,
    login,
    logout,
    loading,
    isAuthenticated: !!user,
    isAdmin: user?.role === "ADMIN",
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
