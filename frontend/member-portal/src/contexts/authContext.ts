import { createContext } from "react";
import { User } from "../types";

export interface AuthContextType {
  user: User | null;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  loading: boolean;
  isAuthenticated: boolean;
  isAdmin: boolean;
}

// Kept apart from AuthProvider so that file only exports components (fast refresh).
export const AuthContext = createContext<AuthContextType | undefined>(
  undefined,
);
