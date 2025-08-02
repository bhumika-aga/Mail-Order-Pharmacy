import { SignupRequest } from '../../types';

// Use manual mock
jest.mock('../api');

import {
  authService,
  drugService,
  refillService,
  subscriptionService,
} from '../api';

describe('API Services', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('authService', () => {
    it('should login with correct credentials', async () => {
      const result = await authService.login('testuser', 'password');

      expect(authService.login).toHaveBeenCalledWith('testuser', 'password');
      expect(result.data.username).toBe('testuser');
      expect(result.data.token).toBe('mock-jwt-token');
    });

    it('should register new user', async () => {
      const signupData: SignupRequest = {
        username: 'newuser',
        email: 'new@example.com',
        password: 'password123',
        fullName: 'New User',
      };

      const result = await authService.register(signupData);

      expect(authService.register).toHaveBeenCalledWith(signupData);
      expect(result.data.message).toBe('User registered successfully!');
    });

    it('should check username availability', async () => {
      const result = await authService.checkUsernameAvailability('testuser');

      expect(authService.checkUsernameAvailability).toHaveBeenCalledWith('testuser');
      expect(result.data.message).toBe('Username is available');
    });

    it('should check email availability', async () => {
      const result = await authService.checkEmailAvailability('test@example.com');

      expect(authService.checkEmailAvailability).toHaveBeenCalledWith('test@example.com');
      expect(result.data.message).toBe('Email is available');
    });

    it('should validate token', async () => {
      const result = await authService.validateToken('jwt-token');

      expect(authService.validateToken).toHaveBeenCalledWith('jwt-token');
      expect(result.data.token).toBe('jwt-token');
    });

    it('should get all users', async () => {
      const result = await authService.getAllUsers();

      expect(authService.getAllUsers).toHaveBeenCalled();
      expect(Array.isArray(result.data)).toBe(true);
    });

    it('should update user', async () => {
      const updateData: SignupRequest = {
        username: 'updateduser',
        email: 'updated@example.com',
        password: 'newpassword',
        fullName: 'Updated User'
      };
      const result = await authService.updateUser(1, updateData);

      expect(authService.updateUser).toHaveBeenCalledWith(1, updateData);
      expect(result.data.username).toBe('updateduser');
    });

    it('should delete user', async () => {
      const result = await authService.deleteUser(1);

      expect(authService.deleteUser).toHaveBeenCalledWith(1);
      expect(result.data.message).toBe('User deleted successfully!');
    });
  });

  describe('drugService', () => {
    it('should get all drugs', async () => {
      const result = await drugService.getAllValidDrugs();

      expect(drugService.getAllValidDrugs).toHaveBeenCalled();
      expect(Array.isArray(result.data)).toBe(true);
    });

    it('should search drug by ID', async () => {
      const result = await drugService.searchById('DRUG001');

      expect(drugService.searchById).toHaveBeenCalledWith('DRUG001');
      expect(result.data.drugId).toBe('DRUG001');
    });

    it('should search drugs by name', async () => {
      const result = await drugService.searchByName('Aspirin');

      expect(drugService.searchByName).toHaveBeenCalledWith('Aspirin');
      expect(Array.isArray(result.data)).toBe(true);
    });
  });

  describe('subscriptionService', () => {
    it('should get my subscriptions', async () => {
      const result = await subscriptionService.getMySubscriptions();

      expect(subscriptionService.getMySubscriptions).toHaveBeenCalled();
      expect(Array.isArray(result.data)).toBe(true);
    });

    it('should create subscription', async () => {
      const subscriptionData = {
        insurancePolicyNumber: 'INS123',
        insuranceProvider: 'Health Co',
        prescriptionDate: '2023-01-01',
        drugId: 'DRUG001',
        dosagePerDay: '1 tablet',
        prescriptionCourse: 30,
        doctorDetails: 'Dr. Smith',
        refillFrequency: 'MONTHLY' as const,
        memberLocation: 'NY',
      };

      const result = await subscriptionService.subscribe(subscriptionData);

      expect(subscriptionService.subscribe).toHaveBeenCalledWith(subscriptionData);
      expect(result.data.subscriptionId).toBe('SUB001');
    });

    it('should get active subscriptions', async () => {
      const result = await subscriptionService.getActiveSubscriptions();

      expect(subscriptionService.getActiveSubscriptions).toHaveBeenCalled();
      expect(Array.isArray(result.data)).toBe(true);
    });
  });

  describe('refillService', () => {
    it('should get all refill orders', async () => {
      const result = await refillService.getAllRefillOrders();

      expect(refillService.getAllRefillOrders).toHaveBeenCalled();
      expect(Array.isArray(result.data)).toBe(true);
    });

    it('should request adhoc refill', async () => {
      const refillData = {
        memberId: 'MEM123456',
        memberLocation: 'NY',
        prescriptions: [{ prescriptionId: 'PRESC001', drugCode: 'DRUG001', quantity: 1 }],
      };

      const result = await refillService.requestAdhocRefill(refillData);

      expect(refillService.requestAdhocRefill).toHaveBeenCalledWith(refillData);
      expect(result.data.message).toBe('Adhoc refill requested successfully');
    });

    it('should get refill status', async () => {
      const result = await refillService.getRefillStatus();

      expect(refillService.getRefillStatus).toHaveBeenCalled();
      expect(result.data.refillOrderId).toBe('ORDER001');
    });
  });
});