import {
  LocalPharmacy,
  Refresh,
  Subscriptions,
  TrendingUp,
} from "@mui/icons-material";
import {
  Box,
  Button,
  Card,
  CardContent,
  Grid,
  Paper,
  Typography,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import React from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import { refillService, subscriptionService } from "../services/api";

const Dashboard: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  const { data: subscriptions } = useQuery({
    queryKey: ["subscriptions"],
    queryFn: () =>
      subscriptionService.getMySubscriptions().then((res) => res.data),
  });

  const { data: refillOrders } = useQuery({
    queryKey: ["refillOrders"],
    queryFn: () => refillService.getAllRefillOrders().then((res) => res.data),
  });

  const activeSubscriptions =
    Array.isArray(subscriptions) ? subscriptions.filter((s) => s.subscriptionStatus === "ACTIVE") : [];
  const pendingRefills =
    Array.isArray(refillOrders) ? refillOrders.filter((r) => r.orderStatus === "PENDING") : [];

  const dashboardCards = [
    {
      title: "Active Subscriptions",
      value: activeSubscriptions.length,
      icon: <Subscriptions sx={{ fontSize: 40 }} />,
      color: "#1976d2",
      action: () => navigate("/subscriptions"),
    },
    {
      title: "Pending Refills",
      value: pendingRefills.length,
      icon: <Refresh sx={{ fontSize: 40 }} />,
      color: "#ed6c02",
      action: () => navigate("/refills"),
    },
    {
      title: "Available Drugs",
      value: "Browse",
      icon: <LocalPharmacy sx={{ fontSize: 40 }} />,
      color: "#2e7d32",
      action: () => navigate("/drugs"),
    },
    {
      title: "Health Analytics",
      value: "View",
      icon: <TrendingUp sx={{ fontSize: 40 }} />,
      color: "#9c27b0",
      action: () => navigate("/dashboard"),
    },
  ];

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Welcome back, {user?.username}!
      </Typography>
      <Typography variant="body1" color="text.secondary" gutterBottom>
        Member ID: {user?.memberId}
      </Typography>

      <Grid container spacing={3} sx={{ mt: 2 }}>
        {dashboardCards.map((card, index) => (
          <Grid item xs={12} sm={6} md={3} key={index}>
            <Card
              sx={{
                cursor: "pointer",
                transition: "transform 0.2s",
                "&:hover": {
                  transform: "translateY(-4px)",
                },
              }}
              onClick={card.action}
            >
              <CardContent>
                <Box
                  sx={{
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "space-between",
                  }}
                >
                  <Box>
                    <Typography color="text.secondary" gutterBottom>
                      {card.title}
                    </Typography>
                    <Typography variant="h4" component="div">
                      {card.value}
                    </Typography>
                  </Box>
                  <Box sx={{ color: card.color }}>{card.icon}</Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Grid container spacing={3} sx={{ mt: 4 }}>
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Recent Activity
            </Typography>
            {activeSubscriptions.length > 0 ? (
              activeSubscriptions.slice(0, 3).map((subscription) => (
                <Box
                  key={subscription.subscriptionId}
                  sx={{
                    display: "flex",
                    justifyContent: "space-between",
                    py: 1,
                    borderBottom: "1px solid #e0e0e0",
                  }}
                >
                  <Typography variant="body2">
                    Subscription {subscription.subscriptionId}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {subscription.refillFrequency}
                  </Typography>
                </Box>
              ))
            ) : (
              <Typography variant="body2" color="text.secondary">
                No active subscriptions
              </Typography>
            )}
            <Button
              fullWidth
              variant="outlined"
              sx={{ mt: 2 }}
              onClick={() => navigate("/subscriptions")}
            >
              View All Subscriptions
            </Button>
          </Paper>
        </Grid>

        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Quick Actions
            </Typography>
            <Box sx={{ display: "flex", flexDirection: "column", gap: 1 }}>
              <Button
                variant="contained"
                fullWidth
                onClick={() => navigate("/drugs")}
              >
                Search Drugs
              </Button>
              <Button
                variant="outlined"
                fullWidth
                onClick={() => navigate("/subscriptions")}
              >
                Manage Subscriptions
              </Button>
              <Button
                variant="outlined"
                fullWidth
                onClick={() => navigate("/refills")}
              >
                Request Refill
              </Button>
            </Box>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Dashboard;
