import { Box, CircularProgress, CircularProgressProps } from "@mui/material";
import React from "react";

interface LoadingSpinnerProps {
  size?: number;
  color?: CircularProgressProps["color"];
}

const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({ size, color }) => {
  return (
    <Box
      role="status"
      aria-live="polite"
      aria-label="Loading content"
      sx={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        minHeight: "200px",
      }}
    >
      <CircularProgress size={size} color={color} />
    </Box>
  );
};

export default LoadingSpinner;
