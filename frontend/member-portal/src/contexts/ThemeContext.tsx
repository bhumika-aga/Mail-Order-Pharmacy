import { createTheme } from '@mui/material/styles';
import React, { createContext, useContext, useEffect, useState } from 'react';

type ThemeMode = 'light' | 'dark';

interface ThemeContextType {
  mode: ThemeMode;
  toggleTheme: () => void;
  theme: any;
}

const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

const getDesignTokens = (mode: ThemeMode) => ({
  palette: {
    mode,
    ...(mode === 'light'
      ? {
          primary: {
            main: '#3b82f6',
            light: '#60a5fa',
            dark: '#1d4ed8',
            contrastText: '#ffffff',
          },
          secondary: {
            main: '#10b981',
            light: '#34d399',
            dark: '#059669',
            contrastText: '#ffffff',
          },
          background: {
            default: '#f8fafc',
            paper: '#ffffff',
          },
          text: {
            primary: '#0f172a',
            secondary: '#475569',
          },
          divider: '#e2e8f0',
          action: {
            hover: '#f1f5f9',
            selected: '#e2e8f0',
            disabled: '#cbd5e1',
            disabledBackground: '#f1f5f9',
          },
          grey: {
            50: '#f8fafc',
            100: '#f1f5f9',
            200: '#e2e8f0',
            300: '#cbd5e1',
            400: '#94a3b8',
            500: '#64748b',
            600: '#475569',
            700: '#334155',
            800: '#1e293b',
            900: '#0f172a',
          },
          success: {
            main: '#10b981',
            light: '#34d399',
            dark: '#059669',
            contrastText: '#ffffff',
          },
          warning: {
            main: '#f59e0b',
            light: '#fbbf24',
            dark: '#d97706',
            contrastText: '#ffffff',
          },
          error: {
            main: '#ef4444',
            light: '#f87171',
            dark: '#dc2626',
            contrastText: '#ffffff',
          },
          info: {
            main: '#3b82f6',
            light: '#60a5fa',
            dark: '#1d4ed8',
            contrastText: '#ffffff',
          },
        }
      : {
          primary: {
            main: '#60a5fa',
            light: '#93c5fd',
            dark: '#3b82f6',
            contrastText: '#ffffff',
          },
          secondary: {
            main: '#34d399',
            light: '#6ee7b7',
            dark: '#10b981',
          },
          background: {
            default: '#0f172a',
            paper: '#1e293b',
          },
          text: {
            primary: '#f1f5f9',
            secondary: '#cbd5e1',
          },
          grey: {
            50: '#0f172a',
            100: '#1e293b',
            200: '#334155',
            300: '#475569',
            400: '#64748b',
            500: '#94a3b8',
            600: '#cbd5e1',
            700: '#e2e8f0',
            800: '#f1f5f9',
            900: '#f8fafc',
          },
        }),
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
    h1: {
      fontWeight: 700,
      fontSize: '2.25rem',
      lineHeight: 1.2,
    },
    h2: {
      fontWeight: 600,
      fontSize: '1.875rem',
      lineHeight: 1.3,
    },
    h3: {
      fontWeight: 600,
      fontSize: '1.5rem',
      lineHeight: 1.4,
    },
    h4: {
      fontWeight: 600,
      fontSize: '1.25rem',
      lineHeight: 1.4,
    },
    h5: {
      fontWeight: 600,
      fontSize: '1.125rem',
      lineHeight: 1.5,
    },
    h6: {
      fontWeight: 600,
      fontSize: '1rem',
      lineHeight: 1.5,
    },
    body1: {
      fontSize: '1rem',
      lineHeight: 1.5,
    },
    body2: {
      fontSize: '0.875rem',
      lineHeight: 1.43,
    },
  },
  shape: {
    borderRadius: 12,
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none' as const,
          fontWeight: 600,
          borderRadius: 12,
          boxShadow: 'none',
          padding: '8px 20px',
          fontSize: '0.875rem',
          transition: 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
          '&:hover': {
            transform: 'translateY(-1px)',
            boxShadow: mode === 'light' ? 
              '0 4px 12px -2px rgba(0, 0, 0, 0.1), 0 2px 4px -2px rgba(0, 0, 0, 0.06)' :
              '0 4px 12px -2px rgba(0, 0, 0, 0.3), 0 2px 4px -2px rgba(0, 0, 0, 0.4)',
          },
        },
        contained: {
          boxShadow: mode === 'light' ? 
            '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06)' :
            '0 1px 3px 0 rgba(0, 0, 0, 0.3), 0 1px 2px 0 rgba(0, 0, 0, 0.4)',
          '&:hover': {
            boxShadow: mode === 'light' ? 
              '0 10px 25px -5px rgba(0, 0, 0, 0.15), 0 4px 6px -2px rgba(0, 0, 0, 0.1)' :
              '0 10px 25px -5px rgba(0, 0, 0, 0.5), 0 4px 6px -2px rgba(0, 0, 0, 0.6)',
          },
        },
        outlined: {
          borderWidth: '1.5px',
          borderColor: mode === 'light' ? '#e2e8f0' : 'rgba(255, 255, 255, 0.12)',
          backgroundColor: mode === 'light' ? '#ffffff' : 'rgba(255, 255, 255, 0.05)',
          '&:hover': {
            borderWidth: '1.5px',
            backgroundColor: mode === 'light' ? '#f8fafc' : 'rgba(255, 255, 255, 0.08)',
          },
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          border: mode === 'light' ? '1px solid #e2e8f0' : '1px solid rgba(255, 255, 255, 0.08)',
          borderRadius: 16,
          backgroundColor: mode === 'light' ? '#ffffff' : '#1e293b',
          boxShadow: mode === 'light' ? 
            '0 1px 3px 0 rgba(0, 0, 0, 0.12), 0 1px 2px 0 rgba(0, 0, 0, 0.08)' :
            '0 1px 3px 0 rgba(0, 0, 0, 0.3), 0 1px 2px 0 rgba(0, 0, 0, 0.4)',
          transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
          '&:hover': {
            transform: 'translateY(-2px)',
            borderColor: mode === 'light' ? '#cbd5e1' : 'rgba(255, 255, 255, 0.12)',
            boxShadow: mode === 'light' ? 
              '0 10px 25px -5px rgba(0, 0, 0, 0.15), 0 4px 6px -2px rgba(0, 0, 0, 0.1)' :
              '0 10px 25px -5px rgba(0, 0, 0, 0.5), 0 4px 6px -2px rgba(0, 0, 0, 0.6)',
          },
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          borderRadius: 16,
          border: mode === 'light' ? '1px solid rgba(0, 0, 0, 0.08)' : '1px solid rgba(255, 255, 255, 0.08)',
          boxShadow: mode === 'light' ? 
            '0 1px 3px 0 rgba(0, 0, 0, 0.05), 0 1px 2px 0 rgba(0, 0, 0, 0.05)' :
            '0 1px 3px 0 rgba(0, 0, 0, 0.3), 0 1px 2px 0 rgba(0, 0, 0, 0.4)',
        },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          borderBottom: mode === 'light' ? '1px solid rgba(0, 0, 0, 0.08)' : '1px solid rgba(255, 255, 255, 0.08)',
          boxShadow: mode === 'light' ? 
            '0 1px 3px 0 rgba(0, 0, 0, 0.05), 0 1px 2px 0 rgba(0, 0, 0, 0.03)' : 
            'none',
          background: mode === 'light' ? 
            'rgba(255, 255, 255, 0.9)' : 
            'rgba(30, 41, 59, 0.8)',
          backdropFilter: 'blur(20px)',
          borderRadius: 0,
          color: mode === 'light' ? '#0f172a' : '#f1f5f9',
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            borderRadius: 12,
            backgroundColor: mode === 'light' ? '#ffffff' : 'rgba(255, 255, 255, 0.05)',
            '& fieldset': {
              borderColor: mode === 'light' ? '#e2e8f0' : 'rgba(255, 255, 255, 0.12)',
            },
            '&:hover fieldset': {
              borderColor: mode === 'light' ? '#cbd5e1' : 'rgba(255, 255, 255, 0.2)',
            },
            '&.Mui-focused fieldset': {
              borderWidth: 2,
              borderColor: mode === 'light' ? '#3b82f6' : '#60a5fa',
            },
          },
        },
      },
    },
    MuiChip: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          fontWeight: 500,
          backgroundColor: mode === 'light' ? '#f1f5f9' : 'rgba(255, 255, 255, 0.08)',
          color: mode === 'light' ? '#475569' : '#cbd5e1',
          border: mode === 'light' ? '1px solid #e2e8f0' : '1px solid rgba(255, 255, 255, 0.12)',
        },
        filled: {
          backgroundColor: mode === 'light' ? '#3b82f6' : '#60a5fa',
          color: '#ffffff',
        },
      },
    },
    MuiMenu: {
      styleOverrides: {
        paper: {
          borderRadius: 12,
          border: mode === 'light' ? '1px solid #e2e8f0' : '1px solid rgba(255, 255, 255, 0.08)',
          boxShadow: mode === 'light' ? 
            '0 10px 25px -5px rgba(0, 0, 0, 0.15), 0 4px 6px -2px rgba(0, 0, 0, 0.1)' :
            '0 10px 25px -5px rgba(0, 0, 0, 0.5), 0 4px 6px -2px rgba(0, 0, 0, 0.6)',
          backdropFilter: 'blur(20px)',
          backgroundColor: mode === 'light' ? 'rgba(255, 255, 255, 0.95)' : 'rgba(30, 41, 59, 0.95)',
        },
      },
    },
    MuiMenuItem: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          margin: '4px 8px',
          '&:hover': {
            backgroundColor: mode === 'light' ? '#f1f5f9' : 'rgba(255, 255, 255, 0.05)',
          },
          '&.Mui-selected': {
            backgroundColor: mode === 'light' ? '#e2e8f0' : 'rgba(255, 255, 255, 0.08)',
          },
        },
      },
    },
  },
});

export const ThemeProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [mode, setMode] = useState<ThemeMode>(() => {
    const savedMode = localStorage.getItem('theme-mode');
    return (savedMode as ThemeMode) || 'light';
  });

  const theme = React.useMemo(() => createTheme(getDesignTokens(mode)), [mode]);

  const toggleTheme = () => {
    const newMode = mode === 'light' ? 'dark' : 'light';
    setMode(newMode);
    localStorage.setItem('theme-mode', newMode);
  };

  useEffect(() => {
    localStorage.setItem('theme-mode', mode);
  }, [mode]);

  const contextValue: ThemeContextType = {
    mode,
    toggleTheme,
    theme,
  };

  return (
    <ThemeContext.Provider value={contextValue}>
      {children}
    </ThemeContext.Provider>
  );
};

export const useTheme = (): ThemeContextType => {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error('useTheme must be used within a ThemeProvider');
  }
  return context;
};