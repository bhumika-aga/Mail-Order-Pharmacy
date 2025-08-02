import React from 'react';
import { Typography, Container } from '@mui/material';

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