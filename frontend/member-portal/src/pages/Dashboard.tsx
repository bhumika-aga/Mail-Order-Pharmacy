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

  const activeSubscriptions = Array.isArray(subscriptions)
    ? subscriptions.filter((s) => s.subscriptionStatus === "ACTIVE")
    : [];
  const pendingRefills = Array.isArray(refillOrders)
    ? refillOrders.filter((r) => r.orderStatus === "PENDING")
    : [];

  const dashboardCards = [
    {
      title: "Active Subscriptions",
      value: activeSubscriptions.length,
      icon: <Subscriptions sx={{ fontSize: 32 }} />,
      color: "#3b82f6",
      bgColor: "#eff6ff",
      action: () => navigate("/subscriptions"),
    },
    {
      title: "Pending Refills",
      value: pendingRefills.length,
      icon: <Refresh sx={{ fontSize: 32 }} />,
      color: "#f59e0b",
      bgColor: "#fffbeb",
      action: () => navigate("/refills"),
    },
    {
      title: "Available Drugs",
      value: "Browse",
      icon: <LocalPharmacy sx={{ fontSize: 32 }} />,
      color: "#10b981",
      bgColor: "#ecfdf5",
      action: () => navigate("/drugs"),
    },
    {
      title: "Health Analytics",
      value: "View",
      icon: <TrendingUp sx={{ fontSize: 32 }} />,
      color: "#8b5cf6",
      bgColor: "#f5f3ff",
      action: () => navigate("/dashboard"),
    },
  ];

  return (
    <Box>
      <Box sx={{ mb: 4 }}>
        <Typography
          variant="h4"
          gutterBottom
          sx={{
            fontWeight: 700,
            color: "#1e293b",
            mb: 1,
          }}
        >
          Welcome back, {user?.username}!
        </Typography>
        <Typography
          variant="body1"
          sx={{
            color: "#64748b",
            fontSize: "1rem",
          }}
        >
          Member ID: {user?.memberId}
        </Typography>
      </Box>

      <Grid container spacing={3} sx={{ mt: 2 }}>
        {dashboardCards.map((card, index) => (
          <Grid item xs={12} sm={6} md={3} key={index}>
            <Card
              sx={{
                cursor: "pointer",
                height: "100%",
                display: "flex",
                flexDirection: "column",
              }}
              onClick={card.action}
            >
              <CardContent sx={{ flexGrow: 1, p: 3 }}>
                <Box
                  sx={{
                    display: "flex",
                    alignItems: "flex-start",
                    justifyContent: "space-between",
                    mb: 2,
                  }}
                >
                  <Box sx={{ flexGrow: 1 }}>
                    <Typography
                      variant="body2"
                      sx={{
                        color: "#64748b",
                        fontWeight: 500,
                        mb: 1,
                        textTransform: "uppercase",
                        letterSpacing: "0.5px",
                        fontSize: "0.75rem",
                      }}
                    >
                      {card.title}
                    </Typography>
                    <Typography
                      variant="h3"
                      component="div"
                      sx={{
                        fontWeight: 700,
                        color: "#1e293b",
                        fontSize: "2rem",
                        lineHeight: 1.2,
                      }}
                    >
                      {card.value}
                    </Typography>
                  </Box>
                  <Box
                    sx={{
                      backgroundColor: card.bgColor,
                      borderRadius: 2,
                      p: 1.5,
                      color: card.color,
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                    }}
                  >
                    {card.icon}
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Grid container spacing={3} sx={{ mt: 4 }}>
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 3, height: "fit-content" }}>
            <Typography
              variant="h6"
              gutterBottom
              sx={{
                fontWeight: 600,
                color: "#1e293b",
                mb: 2,
              }}
            >
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
          <Paper sx={{ p: 3, height: "fit-content" }}>
            <Typography
              variant="h6"
              gutterBottom
              sx={{
                fontWeight: 600,
                color: "#1e293b",
                mb: 2,
              }}
            >
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
