import {
  Visibility,
  VisibilityOff,
  PersonAdd,
  CheckCircle,
  Error,
} from "@mui/icons-material";
import {
  Box,
  Button,
  Card,
  CardContent,
  Container,
  IconButton,
  InputAdornment,
  TextField,
  Typography,
  Alert,
  Link,
  Grid,
} from "@mui/material";
import { useMutation } from "@tanstack/react-query";
import React, { useState } from "react";
import { Link as RouterLink, useNavigate } from "react-router-dom";
import { authService } from "../services/api";
import { SignupRequest } from "../types";

const SignupPage: React.FC = () => {
  const [formData, setFormData] = useState<SignupRequest>({
    username: "",
    email: "",
    password: "",
    fullName: "",
  });
  const [showPassword, setShowPassword] = useState(false);
  const [fieldValidation, setFieldValidation] = useState({
    username: { isValid: true, message: "" },
    email: { isValid: true, message: "" },
  });

  const navigate = useNavigate();

  const signupMutation = useMutation({
    mutationFn: (data: SignupRequest) => authService.register(data),
    onSuccess: () => {
      navigate("/login", { 
        state: { message: "Registration successful! Please log in with your credentials." }
      });
    },
    onError: (error) => {
      console.error("Registration error:", error);
    },
  });

  const checkAvailability = async (field: string, value: string) => {
    if (!value.trim()) return;

    try {
      let response;
      switch (field) {
        case "username":
          response = await authService.checkUsernameAvailability(value);
          break;
        case "email":
          response = await authService.checkEmailAvailability(value);
          break;
        default:
          return;
      }

      const isAvailable = response.data.message.includes("available");
      let message = response.data.message;
      
      // Show "Email is valid" for successful email validation
      if (field === "email" && isAvailable) {
        message = "Email is valid";
      }
      
      setFieldValidation(prev => ({
        ...prev,
        [field]: {
          isValid: isAvailable,
          message: message
        }
      }));
    } catch (error) {
      console.error(`Error checking ${field} availability:`, error);
      setFieldValidation(prev => ({
        ...prev,
        [field]: {
          isValid: false,
          message: "Error checking availability"
        }
      }));
    }
  };

  const handleInputChange = (field: string, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    
    // Clear validation message when user starts typing
    if (fieldValidation[field as keyof typeof fieldValidation]) {
      setFieldValidation(prev => ({
        ...prev,
        [field]: { isValid: true, message: "" }
      }));
    }
  };

  const handleBlur = (field: string, value: string) => {
    if (["username", "email"].includes(field)) {
      checkAvailability(field, value);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    // Basic validation
    if (!formData.username || !formData.email || !formData.password || !formData.fullName) {
      return;
    }

    // Check if any field validation failed
    const hasValidationErrors = Object.values(fieldValidation).some(field => !field.isValid);
    if (hasValidationErrors) {
      return;
    }

    signupMutation.mutate(formData);
  };

  const getFieldStatus = (field: keyof typeof fieldValidation) => {
    const validation = fieldValidation[field];
    if (!validation.message) return null;
    
    return validation.isValid ? (
      <Box display="flex" alignItems="center" gap={0.5} mt={0.5}>
        <CheckCircle color="success" fontSize="small" />
        <Typography variant="caption" color="success.main">
          {validation.message}
        </Typography>
      </Box>
    ) : (
      <Box display="flex" alignItems="center" gap={0.5} mt={0.5}>
        <Error color="error" fontSize="small" />
        <Typography variant="caption" color="error">
          {validation.message}
        </Typography>
      </Box>
    );
  };

  return (
    <Container maxWidth="sm">
      <Box
        display="flex"
        flexDirection="column"
        alignItems="center"
        justifyContent="center"
        minHeight="100vh"
        py={4}
      >
        <Card sx={{ width: "100%", maxWidth: 500 }}>
          <CardContent sx={{ p: 4 }}>
            <>
              <Box display="flex" flexDirection="column" alignItems="center" mb={3}>
                <PersonAdd sx={{ fontSize: 48, color: "primary.main", mb: 2 }} />
                <Typography variant="h4" component="h1" gutterBottom>
                  Create Account
                </Typography>
                <Typography variant="body2" color="text.secondary" textAlign="center">
                  Join the Mail-Order Pharmacy system
                </Typography>
              </Box>

              {signupMutation.error && (
                <Alert severity="error" sx={{ mb: 3 }}>
                  Registration failed. Please try again.
                </Alert>
              )}

              <Box component="form" onSubmit={handleSubmit}>
              <Grid container spacing={2}>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="Username"
                    value={formData.username}
                    onChange={(e) => handleInputChange("username", e.target.value)}
                    onBlur={(e) => handleBlur("username", e.target.value)}
                    required
                    error={!fieldValidation.username.isValid && !!fieldValidation.username.message}
                    helperText="3-20 characters"
                    inputProps={{ minLength: 3, maxLength: 20 }}
                  />
                  {getFieldStatus("username")}
                </Grid>

                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="Full Name"
                    value={formData.fullName}
                    onChange={(e) => handleInputChange("fullName", e.target.value)}
                    required
                    helperText="2-100 characters"
                    inputProps={{ minLength: 2, maxLength: 100 }}
                  />
                </Grid>

                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Email"
                    type="email"
                    value={formData.email}
                    onChange={(e) => handleInputChange("email", e.target.value)}
                    onBlur={(e) => handleBlur("email", e.target.value)}
                    required
                    error={!fieldValidation.email.isValid && !!fieldValidation.email.message}
                    helperText="Valid email address"
                  />
                  {getFieldStatus("email")}
                </Grid>

                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Password"
                    type={showPassword ? "text" : "password"}
                    value={formData.password}
                    onChange={(e) => handleInputChange("password", e.target.value)}
                    required
                    helperText="Minimum 6 characters"
                    inputProps={{ minLength: 6, maxLength: 40 }}
                    InputProps={{
                      endAdornment: (
                        <InputAdornment position="end">
                          <IconButton
                            onClick={() => setShowPassword(!showPassword)}
                            edge="end"
                          >
                            {showPassword ? <VisibilityOff /> : <Visibility />}
                          </IconButton>
                        </InputAdornment>
                      ),
                    }}
                  />
                </Grid>
              </Grid>

              <Button
                type="submit"
                fullWidth
                variant="contained"
                size="large"
                disabled={signupMutation.isPending}
                sx={{ mt: 3, mb: 2 }}
              >
                {signupMutation.isPending ? "Creating Account..." : "Create Account"}
              </Button>

              <Box textAlign="center">
                <Typography variant="body2" color="text.secondary">
                  Already have an account?{" "}
                  <Link component={RouterLink} to="/login">
                    Sign in here
                  </Link>
                </Typography>
              </Box>
              </Box>
            </>
          </CardContent>
        </Card>
      </Box>
    </Container>
  );
};

export default SignupPage;