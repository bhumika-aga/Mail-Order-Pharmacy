import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Container,
  Divider,
  Link,
  Paper,
  TextField,
  Typography,
} from "@mui/material";
import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { Link as RouterLink, useLocation } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";

interface LoginFormData {
  username: string;
  password: string;
}

const LoginForm: React.FC = () => {
  const { login } = useAuth();
  const [error, setError] = useState<string>("");
  const [loading, setLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState<string>("");
  const location = useLocation();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>();

  useEffect(() => {
    if (location.state?.message) {
      setSuccessMessage(location.state.message);
    }
  }, [location.state]);

  const onSubmit = async (data: LoginFormData) => {
    setLoading(true);
    setError("");
    setSuccessMessage("");

    try {
      await login(data.username, data.password);
    } catch (err) {
      setError("Invalid username or password");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container component="main" maxWidth="sm">
      <Box
        sx={{
          marginTop: 8,
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
        }}
      >
        <Paper elevation={3} sx={{ padding: 4, width: "100%" }}>
          <Typography
            component="h1"
            variant="h4"
            align="center"
            gutterBottom
            sx={{
              fontWeight: 700,
              background: "linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%)",
              WebkitBackgroundClip: "text",
              WebkitTextFillColor: "transparent",
              mb: 1,
            }}
          >
            MediFlow
          </Typography>
          <Typography
            component="h2"
            variant="h6"
            align="center"
            color="text.secondary"
            gutterBottom
          >
            Member Portal Login
          </Typography>

          {successMessage && (
            <Alert severity="success" sx={{ mb: 2 }}>
              {successMessage}
            </Alert>
          )}

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          <Box
            component="form"
            onSubmit={handleSubmit(onSubmit)}
            sx={{ mt: 1 }}
          >
            <TextField
              margin="normal"
              required
              fullWidth
              id="username"
              label="Username"
              autoComplete="username"
              autoFocus
              {...register("username", { required: "Username is required" })}
              error={!!errors.username}
              helperText={errors.username?.message}
            />
            <TextField
              margin="normal"
              required
              fullWidth
              label="Password"
              type="password"
              id="password"
              autoComplete="current-password"
              {...register("password", { required: "Password is required" })}
              error={!!errors.password}
              helperText={errors.password?.message}
            />
            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
              disabled={loading}
            >
              {loading ? <CircularProgress size={24} /> : "Sign In"}
            </Button>

            <Box textAlign="center" sx={{ mb: 2 }}>
              <Typography variant="body2" color="text.secondary">
                Don't have an account?{" "}
                <Link component={RouterLink} to="/signup">
                  Create one here
                </Link>
              </Typography>
            </Box>
          </Box>

          <Divider sx={{ my: 2 }} />

          <Box
            sx={{ mt: 2, p: 2, backgroundColor: "#f5f5f5", borderRadius: 1 }}
          >
            <Typography variant="body2" color="text.secondary" align="center">
              <strong>Demo Credentials:</strong>
              <br />
              Username: member1 | Password: password
              <br />
              Username: member2 | Password: password
              <br />
              Username: member3 | Password: password
            </Typography>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
};

export default LoginForm;
