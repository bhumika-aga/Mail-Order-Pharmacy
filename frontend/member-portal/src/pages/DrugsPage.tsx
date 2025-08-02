import React from 'react';
import { Typography, Container } from '@mui/material';

const DrugsPage: React.FC = () => {
  return (
    <Container>
      <Typography variant="h4" component="h1" gutterBottom>
        Drug Search
      </Typography>
      <Typography variant="body1">
        Search and view available drugs.
      </Typography>
    </Container>
  );
};

export default DrugsPage;