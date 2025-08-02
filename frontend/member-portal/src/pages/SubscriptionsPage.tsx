import {
  Add,
  Cancel,
  Visibility,
} from "@mui/icons-material";
import {
  Box,
  Button,
  Card,
  CardActions,
  CardContent,
  Chip,
  Container,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  Grid,
  IconButton,
  InputLabel,
  MenuItem,
  Select,
  TextField,
  Typography,
} from "@mui/material";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import React, { useState } from "react";
import LoadingSpinner from "../components/common/LoadingSpinner";
import { subscriptionService } from "../services/api";
import { MemberSubscription, SubscriptionRequest } from "../types";

const SubscriptionsPage: React.FC = () => {
  const [openDialog, setOpenDialog] = useState(false);
  const [viewDialog, setViewDialog] = useState(false);
  const [selectedSubscription, setSelectedSubscription] = useState<MemberSubscription | null>(null);
  const [subscriptionForm, setSubscriptionForm] = useState<SubscriptionRequest>({
    insurancePolicyNumber: "",
    insuranceProvider: "",
    prescriptionDate: "",
    drugId: "",
    dosagePerDay: "",
    prescriptionCourse: 30,
    doctorDetails: "",
    refillFrequency: "MONTHLY",
    memberLocation: "",
  });

  const queryClient = useQueryClient();

  const { data: subscriptions, isLoading } = useQuery({
    queryKey: ["subscriptions"],
    queryFn: () => subscriptionService.getMySubscriptions().then((res) => res.data),
  });

  // Note: prescriptions endpoint may be available for future use

  const subscribeMutation = useMutation({
    mutationFn: (request: SubscriptionRequest) =>
      subscriptionService.subscribe(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["subscriptions"] });
      setOpenDialog(false);
      resetForm();
    },
  });

  const unsubscribeMutation = useMutation({
    mutationFn: (subscriptionId: string) =>
      subscriptionService.unsubscribe(subscriptionId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["subscriptions"] });
    },
  });

  const resetForm = () => {
    setSubscriptionForm({
      insurancePolicyNumber: "",
      insuranceProvider: "",
      prescriptionDate: "",
      drugId: "",
      dosagePerDay: "",
      prescriptionCourse: 30,
      doctorDetails: "",
      refillFrequency: "MONTHLY",
      memberLocation: "",
    });
  };

  const handleSubscribe = () => {
    subscribeMutation.mutate(subscriptionForm);
  };

  const handleUnsubscribe = (subscriptionId: string) => {
    if (window.confirm("Are you sure you want to unsubscribe?")) {
      unsubscribeMutation.mutate(subscriptionId);
    }
  };

  const handleViewDetails = (subscription: MemberSubscription) => {
    setSelectedSubscription(subscription);
    setViewDialog(true);
  };

  if (isLoading) return <LoadingSpinner />;

  return (
    <Container maxWidth="lg">
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h1">
          My Subscriptions
        </Typography>
        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={() => setOpenDialog(true)}
        >
          New Subscription
        </Button>
      </Box>

      <Grid container spacing={3}>
        {Array.isArray(subscriptions) && subscriptions.length > 0 ? (
          subscriptions.map((subscription) => (
            <Grid item xs={12} md={6} lg={4} key={subscription.subscriptionId}>
              <Card>
                <CardContent>
                  <Box display="flex" justifyContent="space-between" alignItems="start" mb={2}>
                    <Typography variant="h6" component="h2">
                      Subscription #{subscription.subscriptionId}
                    </Typography>
                    <Chip
                      label={subscription.subscriptionStatus}
                      color={subscription.subscriptionStatus === "ACTIVE" ? "success" : "default"}
                      size="small"
                    />
                  </Box>
                  <Typography color="text.secondary" gutterBottom>
                    Frequency: {subscription.refillFrequency}
                  </Typography>
                  <Typography color="text.secondary" gutterBottom>
                    Location: {subscription.memberLocation}
                  </Typography>
                  <Typography color="text.secondary">
                    Date: {new Date(subscription.subscriptionDate).toLocaleDateString()}
                  </Typography>
                </CardContent>
                <CardActions>
                  <IconButton
                    size="small"
                    onClick={() => handleViewDetails(subscription)}
                    color="primary"
                  >
                    <Visibility />
                  </IconButton>
                  {subscription.subscriptionStatus === "ACTIVE" && (
                    <IconButton
                      size="small"
                      onClick={() => handleUnsubscribe(subscription.subscriptionId)}
                      color="error"
                    >
                      <Cancel />
                    </IconButton>
                  )}
                </CardActions>
              </Card>
            </Grid>
          ))
        ) : (
          <Grid item xs={12}>
            <Typography variant="body1" color="text.secondary" textAlign="center">
              No subscriptions found. Create your first subscription to get started.
            </Typography>
          </Grid>
        )}
      </Grid>

      {/* New Subscription Dialog */}
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="md" fullWidth>
        <DialogTitle>Create New Subscription</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Insurance Policy Number"
                value={subscriptionForm.insurancePolicyNumber}
                onChange={(e) =>
                  setSubscriptionForm({ ...subscriptionForm, insurancePolicyNumber: e.target.value })
                }
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Insurance Provider"
                value={subscriptionForm.insuranceProvider}
                onChange={(e) =>
                  setSubscriptionForm({ ...subscriptionForm, insuranceProvider: e.target.value })
                }
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Prescription Date"
                type="date"
                value={subscriptionForm.prescriptionDate}
                onChange={(e) =>
                  setSubscriptionForm({ ...subscriptionForm, prescriptionDate: e.target.value })
                }
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Drug ID"
                value={subscriptionForm.drugId}
                onChange={(e) =>
                  setSubscriptionForm({ ...subscriptionForm, drugId: e.target.value })
                }
                placeholder="e.g., D001"
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Dosage Per Day"
                value={subscriptionForm.dosagePerDay}
                onChange={(e) =>
                  setSubscriptionForm({ ...subscriptionForm, dosagePerDay: e.target.value })
                }
                placeholder="e.g., 1 tablet twice daily"
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Prescription Course (days)"
                type="number"
                value={subscriptionForm.prescriptionCourse}
                onChange={(e) =>
                  setSubscriptionForm({ ...subscriptionForm, prescriptionCourse: parseInt(e.target.value) })
                }
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <InputLabel>Refill Frequency</InputLabel>
                <Select
                  value={subscriptionForm.refillFrequency}
                  onChange={(e) =>
                    setSubscriptionForm({ ...subscriptionForm, refillFrequency: e.target.value as "WEEKLY" | "MONTHLY" })
                  }
                >
                  <MenuItem value="WEEKLY">Weekly</MenuItem>
                  <MenuItem value="MONTHLY">Monthly</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Member Location"
                value={subscriptionForm.memberLocation}
                onChange={(e) =>
                  setSubscriptionForm({ ...subscriptionForm, memberLocation: e.target.value })
                }
                placeholder="e.g., NYC, LAX, CHI"
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Doctor Details"
                multiline
                rows={3}
                value={subscriptionForm.doctorDetails}
                onChange={(e) =>
                  setSubscriptionForm({ ...subscriptionForm, doctorDetails: e.target.value })
                }
                placeholder="Doctor name, specialization, hospital, contact"
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button
            onClick={handleSubscribe}
            variant="contained"
            disabled={subscribeMutation.isPending}
          >
            {subscribeMutation.isPending ? "Creating..." : "Create Subscription"}
          </Button>
        </DialogActions>
      </Dialog>

      {/* View Subscription Dialog */}
      <Dialog open={viewDialog} onClose={() => setViewDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Subscription Details</DialogTitle>
        <DialogContent>
          {selectedSubscription && (
            <Box sx={{ mt: 2 }}>
              <Typography variant="h6" gutterBottom>
                Subscription #{selectedSubscription.subscriptionId}
              </Typography>
              <Typography><strong>Status:</strong> {selectedSubscription.subscriptionStatus}</Typography>
              <Typography><strong>Frequency:</strong> {selectedSubscription.refillFrequency}</Typography>
              <Typography><strong>Location:</strong> {selectedSubscription.memberLocation}</Typography>
              <Typography><strong>Date:</strong> {new Date(selectedSubscription.subscriptionDate).toLocaleDateString()}</Typography>
              {selectedSubscription.prescription && (
                <Box mt={2}>
                  <Typography variant="h6" gutterBottom>Prescription Details</Typography>
                  <Typography><strong>Drug ID:</strong> {selectedSubscription.prescription.drugId}</Typography>
                  <Typography><strong>Dosage:</strong> {selectedSubscription.prescription.dosagePerDay}</Typography>
                  <Typography><strong>Course:</strong> {selectedSubscription.prescription.prescriptionCourse} days</Typography>
                  <Typography><strong>Doctor:</strong> {selectedSubscription.prescription.doctorDetails}</Typography>
                </Box>
              )}
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setViewDialog(false)}>Close</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default SubscriptionsPage;