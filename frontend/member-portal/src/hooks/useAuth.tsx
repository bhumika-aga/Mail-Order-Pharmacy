import {
  createContext,
  ReactNode,
  useContext,
  useEffect,
  useState,
} from "react";
import { authService, clearAuthToken, setAuthToken } from "../services/api";
import { AuthResponse, User } from "../types";

interface AuthContextType {
  user: User | null;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  loading: boolean;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      validateToken(token);
    } else {
      setLoading(false);
    }
  }, []);

  const validateToken = async (token: string) => {
    try {
      const response = await authService.validateToken(token);
      const authData = response.data;

      const userData: User = {
        id: 0, // Placeholder ID since auth response doesn't include user ID
        username: authData.username,
        email: "",
        memberId: authData.memberId,
        fullName: "",
        createdAt: "",
        updatedAt: "",
      };

      setUser(userData);
      setAuthToken(token);
    } catch (error) {
      localStorage.removeItem("token");
      clearAuthToken();
    } finally {
      setLoading(false);
    }
  };

  const login = async (username: string, password: string) => {
    try {
      const response = await authService.login(username, password);
      const authData: AuthResponse = response.data;

      localStorage.setItem("token", authData.token);

      const userData: User = {
        id: 0, // Placeholder ID since auth response doesn't include user ID
        username: authData.username,
        email: "",
        memberId: authData.memberId,
        fullName: "",
        createdAt: "",
        updatedAt: "",
      };

      setUser(userData);
      setAuthToken(authData.token);
    } catch (error) {
      throw new Error("Login failed");
    }
  };

  const logout = () => {
    localStorage.removeItem("token");
    setUser(null);
    clearAuthToken();
  };

  const value = {
    user,
    login,
    logout,
    loading,
    isAuthenticated: !!user,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
