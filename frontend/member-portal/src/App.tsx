import { CssBaseline } from "@mui/material";
import { ThemeProvider as MuiThemeProvider } from "@mui/material/styles";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import React from "react";
import {
  Navigate,
  Route,
  BrowserRouter as Router,
  Routes,
} from "react-router-dom";
import LoginForm from "./components/auth/LoginForm";
import Layout from "./components/common/Layout";
import LoadingSpinner from "./components/common/LoadingSpinner";
import { ThemeProvider, useTheme } from "./contexts/ThemeContext";
import { AuthProvider, useAuth } from "./hooks/useAuth";
import Dashboard from "./pages/Dashboard";

// Lazy load pages
const DrugsPage = React.lazy(() => import("./pages/DrugsPage"));
const SubscriptionsPage = React.lazy(() => import("./pages/SubscriptionsPage"));
const RefillsPage = React.lazy(() => import("./pages/RefillsPage"));
const SignupPage = React.lazy(() => import("./pages/SignupPage"));
const UserManagementPage = React.lazy(
  () => import("./pages/UserManagementPage")
);

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
    },
  },
});

const ThemedApp: React.FC = () => {
  const { theme } = useTheme();

  return (
    <MuiThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <Router
          future={{ v7_startTransition: true, v7_relativeSplatPath: true }}
        >
          <AppRoutes />
        </Router>
      </AuthProvider>
    </MuiThemeProvider>
  );
};

const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return <LoadingSpinner />;
  }

  return isAuthenticated ? (
    <Layout>{children}</Layout>
  ) : (
    <Navigate to="/login" />
  );
};

const AppRoutes: React.FC = () => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return <LoadingSpinner />;
  }

  return (
    <Routes>
      <Route
        path="/login"
        element={isAuthenticated ? <Navigate to="/dashboard" /> : <LoginForm />}
      />
      <Route
        path="/signup"
        element={
          isAuthenticated ? (
            <Navigate to="/dashboard" />
          ) : (
            <React.Suspense fallback={<LoadingSpinner />}>
              <SignupPage />
            </React.Suspense>
          )
        }
      />
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <Dashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/drugs"
        element={
          <ProtectedRoute>
            <React.Suspense fallback={<LoadingSpinner />}>
              <DrugsPage />
            </React.Suspense>
          </ProtectedRoute>
        }
      />
      <Route
        path="/subscriptions"
        element={
          <ProtectedRoute>
            <React.Suspense fallback={<LoadingSpinner />}>
              <SubscriptionsPage />
            </React.Suspense>
          </ProtectedRoute>
        }
      />
      <Route
        path="/refills"
        element={
          <ProtectedRoute>
            <React.Suspense fallback={<LoadingSpinner />}>
              <RefillsPage />
            </React.Suspense>
          </ProtectedRoute>
        }
      />
      <Route
        path="/users"
        element={
          <ProtectedRoute>
            <React.Suspense fallback={<LoadingSpinner />}>
              <UserManagementPage />
            </React.Suspense>
          </ProtectedRoute>
        }
      />
      <Route path="/" element={<Navigate to="/dashboard" />} />
    </Routes>
  );
};

const App: React.FC = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider>
        <ThemedApp />
      </ThemeProvider>
    </QueryClientProvider>
  );
};

export default App;
