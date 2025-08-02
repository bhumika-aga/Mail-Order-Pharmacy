import { Container, Typography } from "@mui/material";
import React from "react";

const RefillsPage: React.FC = () => {
  return (
    <Container>
      <Typography variant="h4" component="h1" gutterBottom>
        Refill Management
      </Typography>
      <Typography variant="body1">
        Track and request prescription refills.
      </Typography>
    </Container>
  );
};

export default RefillsPage;
