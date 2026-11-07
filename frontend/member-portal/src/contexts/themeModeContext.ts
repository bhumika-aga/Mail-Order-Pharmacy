import type { Theme } from "@mui/material/styles";
import { createContext, useContext } from "react";

export type ThemeMode = "light" | "dark";

export interface ThemeContextType {
  mode: ThemeMode;
  toggleTheme: () => void;
  theme: Theme;
}

// Kept apart from ThemeProvider so that file only exports components (fast refresh).
export const ThemeContext = createContext<ThemeContextType | undefined>(
  undefined,
);

export const useTheme = (): ThemeContextType => {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error("useTheme must be used within a ThemeProvider");
  }
  return context;
};
