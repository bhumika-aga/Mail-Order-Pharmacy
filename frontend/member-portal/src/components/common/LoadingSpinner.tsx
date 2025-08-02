import { Box, CircularProgress } from "@mui/material";
import React from "react";

const LoadingSpinner: React.FC = () => {
  return (
    <Box
      display="flex"
      justifyContent="center"
      alignItems="center"
      minHeight="200px"
      role="status"
      aria-live="polite"
      aria-label="Loading content"
    >
      <CircularProgress />
    </Box>
  );
};

export default LoadingSpinner;
