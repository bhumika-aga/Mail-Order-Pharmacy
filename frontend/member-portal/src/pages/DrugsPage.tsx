import { CheckCircle, Info, Search, Warning } from "@mui/icons-material";
import {
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
  InputAdornment,
  TextField,
  Typography,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import React, { useState } from "react";
import LoadingSpinner from "../components/common/LoadingSpinner";
import { drugService } from "../services/api";
import { Drug } from "../types";

const DrugsPage: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState("");
  const [searchType, setSearchType] = useState<"all" | "id" | "name">("all");
  const [selectedDrug, setSelectedDrug] = useState<Drug | null>(null);
  const [detailsDialog, setDetailsDialog] = useState(false);

  // Get all drugs
  const { data: allDrugs, isLoading: loadingAll } = useQuery({
    queryKey: ["drugs", "all"],
    queryFn: () => drugService.getAllValidDrugs().then((res) => res.data),
  });

  // Search by ID
  const { data: drugById, isLoading: loadingById } = useQuery({
    queryKey: ["drugs", "id", searchTerm],
    queryFn: () => drugService.searchById(searchTerm).then((res) => res.data),
    enabled: searchType === "id" && searchTerm.length > 0,
  });

  // Search by name
  const { data: drugsByName, isLoading: loadingByName } = useQuery({
    queryKey: ["drugs", "name", searchTerm],
    queryFn: () => drugService.searchByName(searchTerm).then((res) => res.data),
    enabled: searchType === "name" && searchTerm.length > 2,
  });

  const getDrugsToDisplay = () => {
    if (searchType === "all" || !searchTerm) {
      return Array.isArray(allDrugs) ? allDrugs : [];
    }
    if (searchType === "id" && drugById) {
      return [drugById];
    }
    if (searchType === "name" && drugsByName) {
      return Array.isArray(drugsByName) ? drugsByName : [];
    }
    return [];
  };

  const isLoading = (() => {
    if (searchType === "all" || !searchTerm) return loadingAll;
    if (searchType === "id") return loadingById;
    if (searchType === "name") return loadingByName;
    return false;
  })();
  const drugsToDisplay = getDrugsToDisplay();

  const handleSearch = (value: string) => {
    setSearchTerm(value);
    if (value.startsWith("D") && value.length >= 4) {
      setSearchType("id");
    } else if (value.length > 2) {
      setSearchType("name");
    } else {
      setSearchType("all");
    }
  };

  const handleViewDetails = (drug: Drug) => {
    setSelectedDrug(drug);
    setDetailsDialog(true);
  };

  const isExpiringSoon = (expiryDate: string) => {
    const expiry = new Date(expiryDate);
    const today = new Date();
    const diffTime = expiry.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays <= 90; // Expiring within 90 days
  };

  const isExpired = (expiryDate: string) => {
    const expiry = new Date(expiryDate);
    const today = new Date();
    return expiry < today;
  };

  return (
    <Container maxWidth="lg">
      <Typography variant="h4" component="h1" gutterBottom>
        Drug Search
      </Typography>

      {/* Search Box */}
      <Box mb={3}>
        <TextField
          fullWidth
          placeholder="Search by drug name or enter drug ID (e.g., D001)"
          value={searchTerm}
          onChange={(e) => handleSearch(e.target.value)}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <Search />
              </InputAdornment>
            ),
          }}
        />
        <Typography
          variant="caption"
          color="text.secondary"
          sx={{ mt: 1, display: "block" }}
        >
          Enter drug ID (e.g., D001) or drug name to search. Leave empty to see
          all drugs.
        </Typography>
      </Box>

      {/* Loading Spinner */}
      {isLoading && <LoadingSpinner />}

      {/* Results */}
      <Grid container spacing={3}>
        {drugsToDisplay.length > 0 ? (
          drugsToDisplay.map((drug) => (
            <Grid item xs={12} md={6} lg={4} key={drug.drugId}>
              <Card>
                <CardContent>
                  <Box
                    display="flex"
                    justifyContent="space-between"
                    alignItems="start"
                    mb={2}
                  >
                    <Typography variant="h6" component="h2">
                      {drug.drugName}
                    </Typography>
                    <Box display="flex" gap={0.5}>
                      {isExpired(drug.expiryDate) ? (
                        <Chip label="Expired" color="error" size="small" />
                      ) : isExpiringSoon(drug.expiryDate) ? (
                        <Chip
                          label="Expiring Soon"
                          color="warning"
                          size="small"
                        />
                      ) : (
                        <Chip label="Available" color="success" size="small" />
                      )}
                    </Box>
                  </Box>

                  <Typography color="text.secondary" gutterBottom>
                    <strong>ID:</strong> {drug.drugId}
                  </Typography>
                  <Typography color="text.secondary" gutterBottom>
                    <strong>Manufacturer:</strong> {drug.manufacturer}
                  </Typography>
                  <Typography color="text.secondary" gutterBottom>
                    <strong>Price:</strong> ${drug.costPerPackage} per package
                  </Typography>
                  <Typography color="text.secondary" gutterBottom>
                    <strong>Units per Package:</strong> {drug.unitsPerPackage}
                  </Typography>
                  <Typography color="text.secondary" gutterBottom>
                    <strong>Expiry:</strong>{" "}
                    {new Date(drug.expiryDate).toLocaleDateString()}
                  </Typography>

                  <Box mt={2}>
                    <Button
                      variant="outlined"
                      size="small"
                      startIcon={<Info />}
                      onClick={() => handleViewDetails(drug)}
                      fullWidth
                    >
                      View Details
                    </Button>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          ))
        ) : !isLoading ? (
          <Grid item xs={12}>
            <Typography
              variant="body1"
              color="text.secondary"
              textAlign="center"
            >
              {searchTerm
                ? `No drugs found matching "${searchTerm}"`
                : "No drugs available."}
            </Typography>
          </Grid>
        ) : null}
      </Grid>

      {/* Drug Details Dialog */}
      <Dialog
        open={detailsDialog}
        onClose={() => setDetailsDialog(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>Drug Details</DialogTitle>
        <DialogContent>
          {selectedDrug && (
            <Box sx={{ mt: 2 }}>
              <Grid container spacing={3}>
                <Grid item xs={12} sm={6}>
                  <Typography variant="h6" gutterBottom>
                    {selectedDrug.drugName}
                  </Typography>
                  <Typography>
                    <strong>Drug ID:</strong> {selectedDrug.drugId}
                  </Typography>
                  <Typography>
                    <strong>Manufacturer:</strong> {selectedDrug.manufacturer}
                  </Typography>
                  <Typography>
                    <strong>Cost per Package:</strong> $
                    {selectedDrug.costPerPackage}
                  </Typography>
                  <Typography>
                    <strong>Units per Package:</strong>{" "}
                    {selectedDrug.unitsPerPackage}
                  </Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography>
                    <strong>Manufactured Date:</strong>{" "}
                    {new Date(
                      selectedDrug.manufacturedDate
                    ).toLocaleDateString()}
                  </Typography>
                  <Typography>
                    <strong>Expiry Date:</strong>{" "}
                    {new Date(selectedDrug.expiryDate).toLocaleDateString()}
                  </Typography>
                  <Box mt={1}>
                    {isExpired(selectedDrug.expiryDate) ? (
                      <Chip icon={<Warning />} label="Expired" color="error" />
                    ) : isExpiringSoon(selectedDrug.expiryDate) ? (
                      <Chip
                        icon={<Warning />}
                        label="Expiring Soon"
                        color="warning"
                      />
                    ) : (
                      <Chip
                        icon={<CheckCircle />}
                        label="Available"
                        color="success"
                      />
                    )}
                  </Box>
                </Grid>
                <Grid item xs={12}>
                  <Typography variant="h6" gutterBottom sx={{ mt: 2 }}>
                    Medical Composition
                  </Typography>
                  <Typography
                    variant="body2"
                    sx={{
                      backgroundColor: "grey.100",
                      padding: 2,
                      borderRadius: 1,
                      fontFamily: "monospace",
                    }}
                  >
                    {selectedDrug.medicalComposition}
                  </Typography>
                </Grid>
              </Grid>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDetailsDialog(false)}>Close</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default DrugsPage;
