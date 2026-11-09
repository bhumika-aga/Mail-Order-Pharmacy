import { LOCATIONS } from "@/constants";
import { useAuth } from "@/hooks/useAuth";
import { refillService, subscriptionService } from "@/services/api";
import { apiErrorMessage } from "@/services/errors";
import {
  AdhocRefillPrescriptionRequest,
  AdhocRefillRequest,
  RefillStatusResponse,
} from "@/types";
import {
  Add,
  Assignment,
  CalendarToday,
  CheckCircle,
  Info,
  LocalShipping,
  Pending,
  Schedule,
} from "@mui/icons-material";
import type { ChipProps } from "@mui/material";
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Container,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  IconButton,
  MenuItem,
  Tab,
  Tabs,
  TextField,
  Typography,
} from "@mui/material";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { format } from "date-fns";
import React, { useState } from "react";
import LoadingSpinner from "../components/common/LoadingSpinner";

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

const TabPanel: React.FC<TabPanelProps> = ({ children, value, index }) => {
  return (
    <div hidden={value !== index}>
      {value === index && <Box sx={{ pt: 3 }}>{children}</Box>}
    </div>
  );
};

const RefillsPage: React.FC = () => {
  const [tabValue, setTabValue] = useState(0);
  const [selectedDate, setSelectedDate] = useState<Date>(new Date());
  const [adhocDialog, setAdhocDialog] = useState(false);
  const [detailsDialog, setDetailsDialog] = useState(false);
  const [selectedRefill, setSelectedRefill] =
    useState<RefillStatusResponse | null>(null);
  const { user } = useAuth();
  // The member ID is taken from the signed-in user when the request is sent.
  const [adhocForm, setAdhocForm] = useState<
    Omit<AdhocRefillRequest, "memberId">
  >({
    memberLocation: "",
    prescriptions: [
      {
        prescriptionId: "",
        drugCode: "",
        quantity: 1,
      },
    ],
  });

  const queryClient = useQueryClient();

  // Get refill status (latest)
  const { data: refillStatus, isLoading: loadingStatus } = useQuery({
    queryKey: ["refillStatus"],
    queryFn: () => refillService.getRefillStatus().then((res) => res.data),
  });

  // Get all refill orders
  const { data: allRefills, isLoading: loadingAll } = useQuery({
    queryKey: ["refillOrders"],
    queryFn: () => refillService.getAllRefillOrders().then((res) => res.data),
  });

  // Get refill dues for selected date
  // Local calendar date: toISOString() would convert to UTC and, east of UTC,
  // send the previous day.
  const selectedDay = format(selectedDate, "yyyy-MM-dd");

  // The member's own prescriptions, offered in the adhoc refill form
  const { data: prescriptions } = useQuery({
    queryKey: ["prescriptions"],
    queryFn: () =>
      subscriptionService.getPrescriptions().then((res) => res.data),
    enabled: adhocDialog,
  });

  const {
    data: refillDues,
    isLoading: loadingDues,
    error: duesError,
  } = useQuery({
    queryKey: ["refillDues", selectedDay],
    queryFn: () =>
      refillService.getRefillDues(selectedDay).then((res) => res.data),
    enabled: !!selectedDate,
    retry: 2,
  });

  const adhocRefillMutation = useMutation({
    mutationFn: (request: AdhocRefillRequest) =>
      refillService.requestAdhocRefill(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["refillOrders"] });
      queryClient.invalidateQueries({ queryKey: ["refillStatus"] });
      setAdhocDialog(false);
      resetAdhocForm();
    },
  });

  const resetAdhocForm = () => {
    setAdhocForm({
      memberLocation: "",
      prescriptions: [
        {
          prescriptionId: "",
          drugCode: "",
          quantity: 1,
        },
      ],
    });
  };

  const handleRequestAdhoc = () => {
    if (!user?.memberId) return;
    adhocRefillMutation.mutate({ ...adhocForm, memberId: user.memberId });
  };

  const adhocFormComplete =
    !!adhocForm.memberLocation &&
    adhocForm.prescriptions.every(
      (p) => p.prescriptionId && p.drugCode && p.quantity >= 1,
    );

  // Choosing a prescription also sets the drug it covers.
  const selectPrescription = (index: number, prescriptionId: string) => {
    const prescription = prescriptions?.find(
      (p) => p.prescriptionId === prescriptionId,
    );
    const newRequests = [...adhocForm.prescriptions];
    newRequests[index] = {
      ...newRequests[index],
      prescriptionId,
      drugCode: prescription?.drugId ?? "",
    };
    setAdhocForm({ ...adhocForm, prescriptions: newRequests });
  };

  const handleViewDetails = (refill: RefillStatusResponse) => {
    setSelectedRefill(refill);
    setDetailsDialog(true);
  };

  const addPrescriptionRequest = () => {
    setAdhocForm({
      ...adhocForm,
      prescriptions: [
        ...adhocForm.prescriptions,
        { prescriptionId: "", drugCode: "", quantity: 1 },
      ],
    });
  };

  const removePrescriptionRequest = (index: number) => {
    const newRequests = adhocForm.prescriptions.filter((_, i) => i !== index);
    setAdhocForm({ ...adhocForm, prescriptions: newRequests });
  };

  const updatePrescriptionRequest = <
    K extends keyof AdhocRefillPrescriptionRequest,
  >(
    index: number,
    field: K,
    value: AdhocRefillPrescriptionRequest[K],
  ) => {
    const newRequests = [...adhocForm.prescriptions];
    newRequests[index] = { ...newRequests[index], [field]: value };
    setAdhocForm({ ...adhocForm, prescriptions: newRequests });
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case "DELIVERED":
        return <CheckCircle color="success" />;
      case "SHIPPED":
        return <LocalShipping color="info" />;
      case "IN_PROGRESS":
        return <Schedule color="warning" />;
      case "CONFIRMED":
        return <Assignment color="primary" />;
      case "PENDING":
        return <Pending color="disabled" />;
      case "CANCELLED":
        return <Pending color="error" />;
      default:
        return <Pending color="disabled" />;
    }
  };

  const getStatusColor = (status: string): ChipProps["color"] => {
    switch (status) {
      case "DELIVERED":
        return "success";
      case "SHIPPED":
        return "info";
      case "IN_PROGRESS":
        return "warning";
      case "CONFIRMED":
        return "primary";
      case "PENDING":
        return "default";
      case "CANCELLED":
        return "error";
      default:
        return "default";
    }
  };

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <Container maxWidth="lg">
        <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            mb: 3,
          }}
        >
          <Typography variant="h4" component="h1">
            Refill Management
          </Typography>
          <Button
            variant="contained"
            startIcon={<Add />}
            onClick={() => {
              adhocRefillMutation.reset();
              setAdhocDialog(true);
            }}
          >
            Request Adhoc Refill
          </Button>
        </Box>

        {/* Tabs */}
        <Tabs
          value={tabValue}
          onChange={(_, newValue) => setTabValue(newValue)}
        >
          <Tab label="Latest Status" />
          <Tab label="All Refills" />
          <Tab label="Due Refills" />
        </Tabs>

        {/* Latest Refill Status Tab */}
        <TabPanel value={tabValue} index={0}>
          {loadingStatus ? (
            <LoadingSpinner />
          ) : refillStatus ? (
            <Grid container spacing={3}>
              <Grid size={{ xs: 12, md: 8 }}>
                <Card>
                  <CardContent>
                    <Box
                      sx={{
                        display: "flex",
                        alignItems: "center",
                        gap: 2,
                        mb: 2,
                      }}
                    >
                      {getStatusIcon(refillStatus.orderStatus)}
                      <Typography variant="h6">
                        Refill Order #{refillStatus.refillOrderId}
                      </Typography>
                      <Chip
                        label={refillStatus.orderStatus}
                        color={getStatusColor(refillStatus.orderStatus)}
                        size="small"
                      />
                      <Chip
                        label={refillStatus.orderType}
                        color="secondary"
                        size="small"
                        variant="outlined"
                      />
                    </Box>
                    <Grid container spacing={2}>
                      <Grid size={{ xs: 12, sm: 6 }}>
                        <Typography color="text.secondary" gutterBottom>
                          <strong>Order Date:</strong>{" "}
                          {new Date(
                            refillStatus.orderDate,
                          ).toLocaleDateString()}
                        </Typography>
                        <Typography color="text.secondary" gutterBottom>
                          <strong>Member Location:</strong>{" "}
                          {refillStatus.memberLocation}
                        </Typography>
                        {refillStatus.subscriptionId && (
                          <Typography color="text.secondary" gutterBottom>
                            <strong>Subscription ID:</strong>{" "}
                            {refillStatus.subscriptionId}
                          </Typography>
                        )}
                      </Grid>
                      <Grid size={{ xs: 12, sm: 6 }}>
                        <Typography color="text.secondary" gutterBottom>
                          <strong>Total Amount:</strong> ₹
                          {refillStatus.totalAmount?.toFixed(2) || "N/A"}
                        </Typography>
                        {refillStatus.deliveryDate && (
                          <Typography color="text.secondary" gutterBottom>
                            <strong>Delivery Date:</strong>{" "}
                            {new Date(
                              refillStatus.deliveryDate,
                            ).toLocaleDateString()}
                          </Typography>
                        )}
                        {refillStatus.trackingNumber && (
                          <Typography color="text.secondary" gutterBottom>
                            <strong>Tracking:</strong>{" "}
                            {refillStatus.trackingNumber}
                          </Typography>
                        )}
                      </Grid>
                    </Grid>
                    <Box sx={{ mt: 2 }}>
                      <Button
                        variant="outlined"
                        startIcon={<Info />}
                        onClick={() => handleViewDetails(refillStatus)}
                      >
                        View Details
                      </Button>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            </Grid>
          ) : (
            <Typography variant="body1" color="text.secondary">
              No refill status available.
            </Typography>
          )}
        </TabPanel>

        {/* All Refills Tab */}
        <TabPanel value={tabValue} index={1}>
          {loadingAll ? (
            <LoadingSpinner />
          ) : (
            <Grid container spacing={3}>
              {Array.isArray(allRefills) && allRefills.length > 0 ? (
                allRefills.map((refill) => (
                  <Grid
                    key={refill.refillOrderId}
                    size={{ xs: 12, md: 6, lg: 4 }}
                  >
                    <Card>
                      <CardContent>
                        <Box
                          sx={{
                            display: "flex",
                            justifyContent: "space-between",
                            alignItems: "center",
                            mb: 2,
                          }}
                        >
                          <Typography variant="h6">
                            #{refill.refillOrderId}
                          </Typography>
                          <Box sx={{ display: "flex", gap: 0.5 }}>
                            <Chip
                              label={refill.orderStatus}
                              color={getStatusColor(refill.orderStatus)}
                              size="small"
                            />
                            <Chip
                              label={refill.orderType}
                              color="secondary"
                              size="small"
                              variant="outlined"
                            />
                          </Box>
                        </Box>
                        <Typography color="text.secondary" gutterBottom>
                          <strong>Date:</strong>{" "}
                          {new Date(refill.orderDate).toLocaleDateString()}
                        </Typography>
                        <Typography color="text.secondary" gutterBottom>
                          <strong>Location:</strong> {refill.memberLocation}
                        </Typography>
                        <Typography color="text.secondary" gutterBottom>
                          <strong>Amount:</strong> ₹
                          {refill.totalAmount?.toFixed(2) || "N/A"}
                        </Typography>
                        <Typography color="text.secondary" gutterBottom>
                          <strong>Items:</strong>{" "}
                          {refill.lineItems?.length || 0}
                        </Typography>
                        <Box sx={{ mt: 2 }}>
                          <IconButton
                            size="small"
                            onClick={() => handleViewDetails(refill)}
                            color="primary"
                          >
                            <Info />
                          </IconButton>
                        </Box>
                      </CardContent>
                    </Card>
                  </Grid>
                ))
              ) : (
                <Grid size={{ xs: 12 }}>
                  <Typography
                    variant="body1"
                    color="text.secondary"
                    sx={{ textAlign: "center" }}
                  >
                    No refill orders found.
                  </Typography>
                </Grid>
              )}
            </Grid>
          )}
        </TabPanel>

        {/* Due Refills Tab */}
        <TabPanel value={tabValue} index={2}>
          <Box sx={{ mb: 3 }}>
            <DatePicker
              label="Select Date"
              value={selectedDate}
              onChange={(newValue) => newValue && setSelectedDate(newValue)}
              slotProps={{ textField: { fullWidth: false } }}
            />
          </Box>
          {loadingDues ? (
            <LoadingSpinner />
          ) : duesError ? (
            <Box sx={{ textAlign: "center", py: 4 }}>
              <Typography variant="body1" color="error">
                Error loading refill dues:{" "}
                {duesError instanceof Error
                  ? duesError.message
                  : "Unknown error"}
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                Please try selecting a different date or refresh the page.
              </Typography>
            </Box>
          ) : (
            <Grid container spacing={3}>
              {Array.isArray(refillDues) && refillDues.length > 0 ? (
                refillDues.map((due, index) => (
                  <Grid
                    key={`${due.subscriptionId}-${index}`}
                    size={{ xs: 12, md: 6, lg: 4 }}
                  >
                    <Card>
                      <CardContent>
                        <Box
                          sx={{
                            display: "flex",
                            alignItems: "center",
                            gap: 1,
                            mb: 2,
                          }}
                        >
                          <CalendarToday color="warning" />
                          <Typography variant="h6">Due Refill</Typography>
                          <Chip
                            label={due.refillFrequency}
                            color="info"
                            size="small"
                          />
                        </Box>
                        <Typography color="text.secondary" gutterBottom>
                          <strong>Subscription:</strong> {due.subscriptionId}
                        </Typography>
                        <Typography color="text.secondary" gutterBottom>
                          <strong>Member:</strong> {due.memberId}
                        </Typography>
                        <Typography color="text.secondary" gutterBottom>
                          <strong>Location:</strong> {due.memberLocation}
                        </Typography>
                        <Typography color="text.secondary" gutterBottom>
                          <strong>Due Date:</strong>{" "}
                          {new Date(due.dueDate).toLocaleDateString()}
                        </Typography>

                        {due.prescriptions && due.prescriptions.length > 0 && (
                          <Box sx={{ mt: 2 }}>
                            <Typography variant="subtitle2" gutterBottom>
                              Prescriptions:
                            </Typography>
                            {due.prescriptions.map((prescription) => (
                              <Box
                                key={prescription.prescriptionId}
                                sx={{
                                  mb: 1,
                                  p: 1,
                                  bgcolor: "grey.50",
                                  borderRadius: 1,
                                }}
                              >
                                <Typography variant="body2">
                                  <strong>Drug:</strong> {prescription.drugId}
                                </Typography>
                                <Typography variant="body2">
                                  <strong>Dosage:</strong>{" "}
                                  {prescription.dosagePerDay}
                                </Typography>
                              </Box>
                            ))}
                          </Box>
                        )}
                      </CardContent>
                    </Card>
                  </Grid>
                ))
              ) : (
                <Grid size={{ xs: 12 }}>
                  <Typography
                    variant="body1"
                    color="text.secondary"
                    sx={{ textAlign: "center" }}
                  >
                    No refills due for {selectedDate.toLocaleDateString()}.
                  </Typography>
                </Grid>
              )}
            </Grid>
          )}
        </TabPanel>

        {/* Adhoc Refill Dialog */}
        <Dialog
          open={adhocDialog}
          onClose={() => setAdhocDialog(false)}
          maxWidth="md"
          fullWidth
        >
          <DialogTitle>Request Adhoc Refill</DialogTitle>
          <DialogContent>
            {adhocRefillMutation.error && (
              <Alert severity="error" sx={{ mt: 1 }}>
                {apiErrorMessage(
                  adhocRefillMutation.error,
                  "The refill could not be requested. Please try again.",
                )}
              </Alert>
            )}
            <Grid container spacing={2} sx={{ mt: 1 }}>
              <Grid size={{ xs: 12, sm: 6 }}>
                <TextField
                  fullWidth
                  label="Member ID"
                  value={user?.memberId ?? ""}
                  disabled
                  helperText="Your member ID"
                />
              </Grid>
              <Grid size={{ xs: 12, sm: 6 }}>
                <TextField
                  select
                  fullWidth
                  required
                  label="Dispatch Location"
                  value={adhocForm.memberLocation}
                  onChange={(e) =>
                    setAdhocForm({
                      ...adhocForm,
                      memberLocation: e.target.value,
                    })
                  }
                >
                  {LOCATIONS.map((location) => (
                    <MenuItem key={location} value={location}>
                      {location}
                    </MenuItem>
                  ))}
                </TextField>
              </Grid>

              <Grid size={{ xs: 12 }}>
                <Typography variant="h6" gutterBottom>
                  Prescription Requests
                </Typography>
                {adhocForm.prescriptions.map((request, index) => (
                  <Box
                    key={index}
                    sx={{
                      mb: 2,
                      p: 2,
                      border: 1,
                      borderColor: "grey.300",
                      borderRadius: 1,
                    }}
                  >
                    <Grid container spacing={2}>
                      <Grid size={{ xs: 12, sm: 4 }}>
                        <TextField
                          select
                          fullWidth
                          required
                          label="Prescription"
                          value={request.prescriptionId}
                          onChange={(e) =>
                            selectPrescription(index, e.target.value)
                          }
                          helperText={
                            prescriptions && prescriptions.length === 0
                              ? "You have no prescriptions yet"
                              : undefined
                          }
                        >
                          {(prescriptions ?? []).map((p) => (
                            <MenuItem
                              key={p.prescriptionId}
                              value={p.prescriptionId}
                            >
                              {p.prescriptionId}
                            </MenuItem>
                          ))}
                        </TextField>
                      </Grid>
                      <Grid size={{ xs: 12, sm: 4 }}>
                        <TextField
                          fullWidth
                          label="Drug Code"
                          value={request.drugCode}
                          disabled
                          helperText="Set by the prescription"
                        />
                      </Grid>
                      <Grid size={{ xs: 12, sm: 4 }}>
                        <TextField
                          fullWidth
                          label="Quantity"
                          type="number"
                          value={request.quantity}
                          onChange={(e) =>
                            updatePrescriptionRequest(
                              index,
                              "quantity",
                              Math.max(1, parseInt(e.target.value, 10) || 1),
                            )
                          }
                          slotProps={{ htmlInput: { min: 1 } }}
                        />
                      </Grid>
                      {adhocForm.prescriptions.length > 1 && (
                        <Grid size={{ xs: 12 }}>
                          <Button
                            variant="outlined"
                            color="error"
                            size="small"
                            onClick={() => removePrescriptionRequest(index)}
                          >
                            Remove
                          </Button>
                        </Grid>
                      )}
                    </Grid>
                  </Box>
                ))}
                <Button
                  variant="outlined"
                  startIcon={<Add />}
                  onClick={addPrescriptionRequest}
                >
                  Add Another Prescription
                </Button>
              </Grid>
            </Grid>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setAdhocDialog(false)}>Cancel</Button>
            <Button
              onClick={handleRequestAdhoc}
              variant="contained"
              disabled={adhocRefillMutation.isPending || !adhocFormComplete}
            >
              {adhocRefillMutation.isPending
                ? "Requesting..."
                : "Request Refill"}
            </Button>
          </DialogActions>
        </Dialog>

        {/* Refill Details Dialog */}
        <Dialog
          open={detailsDialog}
          onClose={() => setDetailsDialog(false)}
          maxWidth="sm"
          fullWidth
        >
          <DialogTitle>Refill Details</DialogTitle>
          <DialogContent>
            {selectedRefill && (
              <Box sx={{ mt: 2 }}>
                <Typography variant="h6" gutterBottom>
                  Refill Order #{selectedRefill.refillOrderId}
                </Typography>
                <Grid container spacing={2}>
                  <Grid size={{ xs: 12, sm: 6 }}>
                    {selectedRefill.subscriptionId && (
                      <Typography>
                        <strong>Subscription ID:</strong>{" "}
                        {selectedRefill.subscriptionId}
                      </Typography>
                    )}
                    <Typography>
                      <strong>Order Date:</strong>{" "}
                      {new Date(selectedRefill.orderDate).toLocaleDateString()}
                    </Typography>
                    <Typography>
                      <strong>Member:</strong> {selectedRefill.memberId}
                    </Typography>
                    <Typography>
                      <strong>Location:</strong> {selectedRefill.memberLocation}
                    </Typography>
                  </Grid>
                  <Grid size={{ xs: 12, sm: 6 }}>
                    <Typography>
                      <strong>Order Type:</strong> {selectedRefill.orderType}
                    </Typography>
                    <Typography>
                      <strong>Total Amount:</strong> ₹
                      {selectedRefill.totalAmount?.toFixed(2) || "N/A"}
                    </Typography>
                    <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                      {getStatusIcon(selectedRefill.orderStatus)}
                      <Typography>
                        <strong>Status:</strong> {selectedRefill.orderStatus}
                      </Typography>
                    </Box>
                    {selectedRefill.deliveryDate && (
                      <Typography>
                        <strong>Delivery:</strong>{" "}
                        {new Date(
                          selectedRefill.deliveryDate,
                        ).toLocaleDateString()}
                      </Typography>
                    )}
                    {selectedRefill.trackingNumber && (
                      <Typography>
                        <strong>Tracking:</strong>{" "}
                        {selectedRefill.trackingNumber}
                      </Typography>
                    )}
                  </Grid>
                </Grid>

                {selectedRefill.lineItems &&
                selectedRefill.lineItems.length > 0 ? (
                  <Box sx={{ mt: 3 }}>
                    <Typography variant="h6" gutterBottom>
                      Prescription Line Items
                    </Typography>
                    {selectedRefill.lineItems.map((item, index) => (
                      <Box
                        key={`${item.lineItemId}-${index}`}
                        sx={{
                          mb: 2,
                          p: 2,
                          bgcolor: "grey.50",
                          borderRadius: 1,
                        }}
                      >
                        <Grid container spacing={1}>
                          <Grid size={{ xs: 12, sm: 6 }}>
                            <Typography variant="body2">
                              <strong>Drug:</strong> {item.drugName}
                            </Typography>
                            <Typography variant="body2">
                              <strong>Drug Code:</strong> {item.drugCode}
                            </Typography>
                            <Typography variant="body2">
                              <strong>Prescription:</strong>{" "}
                              {item.prescriptionId}
                            </Typography>
                          </Grid>
                          <Grid size={{ xs: 12, sm: 6 }}>
                            <Typography variant="body2">
                              <strong>Quantity:</strong> {item.quantity}
                            </Typography>
                            <Typography variant="body2">
                              <strong>Unit Price:</strong> ₹
                              {item.unitPrice?.toFixed(2) || "N/A"}
                            </Typography>
                            <Typography variant="body2">
                              <strong>Total Price:</strong> ₹
                              {item.totalPrice?.toFixed(2) || "N/A"}
                            </Typography>
                          </Grid>
                        </Grid>
                      </Box>
                    ))}
                  </Box>
                ) : (
                  <Box sx={{ mt: 2 }}>
                    <Typography variant="body2" color="text.secondary">
                      No detailed line items available for this refill order.
                    </Typography>
                  </Box>
                )}
              </Box>
            )}
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setDetailsDialog(false)}>Close</Button>
          </DialogActions>
        </Dialog>
      </Container>
    </LocalizationProvider>
  );
};

export default RefillsPage;
