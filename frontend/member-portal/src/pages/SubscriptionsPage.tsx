import { Container, Typography } from "@mui/material";
import React from "react";

const SubscriptionsPage: React.FC = () => {
  return (
    <Container>
      <Typography variant="h4" component="h1" gutterBottom>
        My Subscriptions
      </Typography>
      <Typography variant="body1">
        Manage your mail-order pharmacy subscriptions.
      </Typography>
    </Container>
  );
};

export default SubscriptionsPage;
