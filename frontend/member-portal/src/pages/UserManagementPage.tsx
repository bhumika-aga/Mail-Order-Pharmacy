import {
  Delete,
  Edit,
  Visibility,
  PersonAdd,
} from "@mui/icons-material";
import {
  Box,
  Button,
  Card,
  CardContent,
  Container,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
  Paper,
  Chip,
  Alert,
  TextField,
  DialogContentText,
} from "@mui/material";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import React, { useState } from "react";
import LoadingSpinner from "../components/common/LoadingSpinner";
import { authService } from "../services/api";
import { User, SignupRequest } from "../types";

const UserManagementPage: React.FC = () => {
  const [viewDialog, setViewDialog] = useState(false);
  const [editDialog, setEditDialog] = useState(false);
  const [deleteDialog, setDeleteDialog] = useState(false);
  const [addDialog, setAddDialog] = useState(false);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [editForm, setEditForm] = useState<SignupRequest>({
    username: "",
    email: "",
    password: "",
    fullName: "",
  });

  const queryClient = useQueryClient();

  // Get all users
  const { data: users, isLoading } = useQuery({
    queryKey: ["users"],
    queryFn: () => authService.getAllUsers().then((res) => res.data),
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => authService.deleteUser(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["users"] });
      setDeleteDialog(false);
      setSelectedUser(null);
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: SignupRequest }) =>
      authService.updateUser(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["users"] });
      setEditDialog(false);
      setSelectedUser(null);
    },
  });

  const addMutation = useMutation({
    mutationFn: (data: SignupRequest) => authService.register(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["users"] });
      setAddDialog(false);
      resetForm();
    },
  });

  const resetForm = () => {
    setEditForm({
      username: "",
      email: "",
      password: "",
      fullName: "",
    });
  };

  const handleView = (user: User) => {
    setSelectedUser(user);
    setViewDialog(true);
  };

  const handleEdit = (user: User) => {
    setSelectedUser(user);
    setEditForm({
      username: user.username,
      email: user.email,
      password: "", // Don't pre-fill password
      fullName: user.fullName || "",
    });
    setEditDialog(true);
  };

  const handleDelete = (user: User) => {
    setSelectedUser(user);
    setDeleteDialog(true);
  };

  const handleAddUser = () => {
    resetForm();
    setAddDialog(true);
  };

  const submitEdit = () => {
    if (selectedUser) {
      const updateData = {
        username: editForm.username,
        email: editForm.email,
        fullName: editForm.fullName,
        password: editForm.password || "unchanged", // Don't update password if empty
      };
      updateMutation.mutate({ id: selectedUser.id, data: updateData });
    }
  };

  const submitAdd = () => {
    addMutation.mutate(editForm);
  };

  const submitDelete = () => {
    if (selectedUser) {
      deleteMutation.mutate(selectedUser.id);
    }
  };

  if (isLoading) return <LoadingSpinner />;

  return (
    <Container maxWidth="lg">
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h1">
          User Management
        </Typography>
        <Button
          variant="contained"
          startIcon={<PersonAdd />}
          onClick={handleAddUser}
        >
          Add New User
        </Button>
      </Box>

      <Card>
        <CardContent>
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Username</TableCell>
                  <TableCell>Email</TableCell>
                  <TableCell>Member ID</TableCell>
                  <TableCell>Created At</TableCell>
                  <TableCell>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {users?.map((user) => (
                  <TableRow key={user.id}>
                    <TableCell>{user.id}</TableCell>
                    <TableCell>{user.username}</TableCell>
                    <TableCell>{user.email}</TableCell>
                    <TableCell>
                      <Chip label={user.memberId} color="primary" size="small" />
                    </TableCell>
                    <TableCell>
                      {new Date(user.createdAt).toLocaleDateString()}
                    </TableCell>
                    <TableCell>
                      <IconButton
                        size="small"
                        onClick={() => handleView(user)}
                        color="info"
                      >
                        <Visibility />
                      </IconButton>
                      <IconButton
                        size="small"
                        onClick={() => handleEdit(user)}
                        color="primary"
                      >
                        <Edit />
                      </IconButton>
                      <IconButton
                        size="small"
                        onClick={() => handleDelete(user)}
                        color="error"
                      >
                        <Delete />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </CardContent>
      </Card>

      {/* View User Dialog */}
      <Dialog open={viewDialog} onClose={() => setViewDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>User Details</DialogTitle>
        <DialogContent>
          {selectedUser && (
            <Grid container spacing={2} sx={{ mt: 1 }}>
              <Grid item xs={12}>
                <Typography><strong>ID:</strong> {selectedUser.id}</Typography>
              </Grid>
              <Grid item xs={12}>
                <Typography><strong>Username:</strong> {selectedUser.username}</Typography>
              </Grid>
              <Grid item xs={12}>
                <Typography><strong>Email:</strong> {selectedUser.email}</Typography>
              </Grid>
              <Grid item xs={12}>
                <Typography><strong>Member ID:</strong> {selectedUser.memberId}</Typography>
              </Grid>
              <Grid item xs={12}>
                <Typography><strong>Created:</strong> {new Date(selectedUser.createdAt).toLocaleString()}</Typography>
              </Grid>
              <Grid item xs={12}>
                <Typography><strong>Updated:</strong> {new Date(selectedUser.updatedAt).toLocaleString()}</Typography>
              </Grid>
            </Grid>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setViewDialog(false)}>Close</Button>
        </DialogActions>
      </Dialog>

      {/* Edit User Dialog */}
      <Dialog open={editDialog} onClose={() => setEditDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Edit User</DialogTitle>
        <DialogContent>
          <>
            {updateMutation.error && (
              <Alert severity="error" sx={{ mb: 2 }}>
                Update failed. Please try again.
              </Alert>
            )}
            <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Username"
                value={editForm.username}
                onChange={(e) => setEditForm({ ...editForm, username: e.target.value })}
                required
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Email"
                type="email"
                value={editForm.email}
                onChange={(e) => setEditForm({ ...editForm, email: e.target.value })}
                required
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Full Name"
                value={editForm.fullName}
                onChange={(e) => setEditForm({ ...editForm, fullName: e.target.value })}
                required
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="New Password (optional)"
                type="password"
                value={editForm.password}
                onChange={(e) => setEditForm({ ...editForm, password: e.target.value })}
                helperText="Leave blank to keep current password"
              />
            </Grid>
            </Grid>
          </>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditDialog(false)}>Cancel</Button>
          <Button
            onClick={submitEdit}
            variant="contained"
            disabled={updateMutation.isPending}
          >
            {updateMutation.isPending ? "Updating..." : "Update"}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Add User Dialog */}
      <Dialog open={addDialog} onClose={() => setAddDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Add New User</DialogTitle>
        <DialogContent>
          <>
            {addMutation.error && (
              <Alert severity="error" sx={{ mb: 2 }}>
                Registration failed. Please try again.
              </Alert>
            )}
            <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Username"
                value={editForm.username}
                onChange={(e) => setEditForm({ ...editForm, username: e.target.value })}
                required
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Email"
                type="email"
                value={editForm.email}
                onChange={(e) => setEditForm({ ...editForm, email: e.target.value })}
                required
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Full Name"
                value={editForm.fullName}
                onChange={(e) => setEditForm({ ...editForm, fullName: e.target.value })}
                required
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Password"
                type="password"
                value={editForm.password}
                onChange={(e) => setEditForm({ ...editForm, password: e.target.value })}
                required
              />
            </Grid>
            </Grid>
          </>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setAddDialog(false)}>Cancel</Button>
          <Button
            onClick={submitAdd}
            variant="contained"
            disabled={addMutation.isPending}
          >
            {addMutation.isPending ? "Creating..." : "Create User"}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteDialog} onClose={() => setDeleteDialog(false)}>
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to delete user "{selectedUser?.username}"? This action cannot be undone.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialog(false)}>Cancel</Button>
          <Button
            onClick={submitDelete}
            color="error"
            variant="contained"
            disabled={deleteMutation.isPending}
          >
            {deleteMutation.isPending ? "Deleting..." : "Delete"}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default UserManagementPage;