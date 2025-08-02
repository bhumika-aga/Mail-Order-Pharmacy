import { AdhocRefillRequest, AxiosResponse, SignupRequest, SubscriptionRequest } from '../../types';

const mockAxiosResponse = <T>(data: T): AxiosResponse<T> => ({
  data,
  status: 200,
  statusText: 'OK',
  headers: {},
  config: {} as any,
});

export const authService = {
  login: jest.fn((username: string, password: string) =>
    Promise.resolve(mockAxiosResponse({
      token: 'mock-jwt-token',
      type: 'Bearer',
      username,
      memberId: 'MEM123456',
      expirationTime: 900000,
    }))
  ),

  register: jest.fn((signupData: SignupRequest) =>
    Promise.resolve(mockAxiosResponse({ message: 'User registered successfully!' }))
  ),

  validateToken: jest.fn((token: string) =>
    Promise.resolve(mockAxiosResponse({
      token,
      type: 'Bearer',
      username: 'testuser',
      memberId: 'MEM123456',
      expirationTime: 900000,
    }))
  ),

  getAllUsers: jest.fn(() =>
    Promise.resolve(mockAxiosResponse([
      { id: 1, username: 'user1', email: 'user1@example.com', memberId: 'MEM123456', fullName: 'User One', createdAt: '2023-01-01', updatedAt: '2023-01-01' }
    ]))
  ),

  getUserById: jest.fn((id: number) =>
    Promise.resolve(mockAxiosResponse({
      id, username: 'user1', email: 'user1@example.com', memberId: 'MEM123456', fullName: 'User One', createdAt: '2023-01-01', updatedAt: '2023-01-01'
    }))
  ),

  updateUser: jest.fn((id: number, userData: SignupRequest) =>
    Promise.resolve(mockAxiosResponse({
      id, username: userData.username, email: userData.email, memberId: 'MEM123456', fullName: userData.fullName, createdAt: '2023-01-01', updatedAt: '2023-01-01'
    }))
  ),

  deleteUser: jest.fn((id: number) =>
    Promise.resolve(mockAxiosResponse({ message: 'User deleted successfully!' }))
  ),

  checkUsernameAvailability: jest.fn((username: string) =>
    Promise.resolve(mockAxiosResponse({ message: 'Username is available' }))
  ),

  checkEmailAvailability: jest.fn((email: string) =>
    Promise.resolve(mockAxiosResponse({ message: 'Email is available' }))
  ),

  checkMemberIdAvailability: jest.fn((memberId: string) =>
    Promise.resolve(mockAxiosResponse({ message: 'Member ID is available' }))
  ),
};

export const drugService = {
  searchById: jest.fn((drugId: string) =>
    Promise.resolve(mockAxiosResponse({
      drugId, drugName: 'Mock Drug', manufacturer: 'Mock Pharma', manufacturedDate: '2023-01-01',
      expiryDate: '2025-01-01', unitsPerPackage: 30, costPerPackage: 15.99, medicalComposition: 'Mock composition'
    }))
  ),

  searchByName: jest.fn((drugName: string) =>
    Promise.resolve(mockAxiosResponse([{
      drugId: 'DRUG001', drugName, manufacturer: 'Mock Pharma', manufacturedDate: '2023-01-01',
      expiryDate: '2025-01-01', unitsPerPackage: 30, costPerPackage: 15.99, medicalComposition: 'Mock composition'
    }]))
  ),

  getAllValidDrugs: jest.fn(() =>
    Promise.resolve(mockAxiosResponse([{
      drugId: 'DRUG001', drugName: 'Mock Drug', manufacturer: 'Mock Pharma', manufacturedDate: '2023-01-01',
      expiryDate: '2025-01-01', unitsPerPackage: 30, costPerPackage: 15.99, medicalComposition: 'Mock composition'
    }]))
  ),

  getDispatchableStock: jest.fn((drugIds: string[], location: string) =>
    Promise.resolve(mockAxiosResponse([{
      drugId: 'DRUG001', drugName: 'Mock Drug', location, quantityAvailable: 100, costPerPackage: 15.99, isAvailable: true
    }]))
  ),

  getDrugLocations: jest.fn((drugId: string) =>
    Promise.resolve(mockAxiosResponse([{ drugId, location: 'New York', quantityAvailable: 100 }]))
  ),
};

export const subscriptionService = {
  subscribe: jest.fn((request: SubscriptionRequest) =>
    Promise.resolve(mockAxiosResponse({
      subscriptionId: 'SUB001', memberId: 'MEM123456', subscriptionDate: '2023-01-01',
      prescriptionId: 'PRESC001', refillFrequency: request.refillFrequency,
      memberLocation: request.memberLocation, subscriptionStatus: 'ACTIVE' as const
    }))
  ),

  unsubscribe: jest.fn((subscriptionId: string) =>
    Promise.resolve(mockAxiosResponse({ message: 'Unsubscribed successfully' }))
  ),

  getMySubscriptions: jest.fn(() =>
    Promise.resolve(mockAxiosResponse([{
      subscriptionId: 'SUB001', memberId: 'MEM123456', subscriptionDate: '2023-01-01',
      prescriptionId: 'PRESC001', refillFrequency: 'MONTHLY' as const,
      memberLocation: 'New York', subscriptionStatus: 'ACTIVE' as const
    }]))
  ),

  getActiveSubscriptions: jest.fn(() =>
    Promise.resolve(mockAxiosResponse([{
      subscriptionId: 'SUB001', memberId: 'MEM123456', subscriptionDate: '2023-01-01',
      prescriptionId: 'PRESC001', refillFrequency: 'MONTHLY' as const,
      memberLocation: 'New York', subscriptionStatus: 'ACTIVE' as const
    }]))
  ),

  getPrescriptions: jest.fn(() =>
    Promise.resolve(mockAxiosResponse([{
      prescriptionId: 'PRESC001', memberId: 'MEM123456', insurancePolicyNumber: 'INS123',
      insuranceProvider: 'Health Co', prescriptionDate: '2023-01-01', drugId: 'DRUG001',
      dosagePerDay: '1 tablet', prescriptionCourse: 30, doctorDetails: 'Dr. Smith'
    }]))
  ),

  getSubscriptionById: jest.fn((subscriptionId: string) =>
    Promise.resolve(mockAxiosResponse({
      subscriptionId, memberId: 'MEM123456', subscriptionDate: '2023-01-01',
      prescriptionId: 'PRESC001', refillFrequency: 'MONTHLY' as const,
      memberLocation: 'New York', subscriptionStatus: 'ACTIVE' as const
    }))
  ),
};

export const refillService = {
  getRefillStatus: jest.fn(() =>
    Promise.resolve(mockAxiosResponse({
      refillOrderId: 'ORDER001', memberId: 'MEM123456', orderDate: '2023-01-01',
      memberLocation: 'New York', orderStatus: 'PENDING' as const, orderType: 'SCHEDULED' as const,
      subscriptionId: 'SUB001', deliveryDate: null, trackingNumber: null, lineItems: [], totalAmount: 99.99
    }))
  ),

  getAllRefillOrders: jest.fn(() =>
    Promise.resolve(mockAxiosResponse([{
      refillOrderId: 'ORDER001', memberId: 'MEM123456', orderDate: '2023-01-01',
      memberLocation: 'New York', orderStatus: 'PENDING' as const, orderType: 'SCHEDULED' as const,
      subscriptionId: 'SUB001', deliveryDate: null, trackingNumber: null, lineItems: [], totalAmount: 99.99
    }]))
  ),

  getRefillDues: jest.fn((date: string) =>
    Promise.resolve(mockAxiosResponse([{
      subscriptionId: 'SUB001', memberId: 'MEM123456', memberLocation: 'New York',
      dueDate: date, refillFrequency: 'MONTHLY', prescriptions: []
    }]))
  ),

  requestAdhocRefill: jest.fn((request: AdhocRefillRequest) =>
    Promise.resolve(mockAxiosResponse({ message: 'Adhoc refill requested successfully' }))
  ),

  getOrders: jest.fn(() =>
    Promise.resolve(mockAxiosResponse([{
      refillOrderId: 'ORDER001', memberId: 'MEM123456', orderDate: '2023-01-01',
      memberLocation: 'New York', orderStatus: 'PENDING' as const, orderType: 'SCHEDULED' as const,
      subscriptionId: 'SUB001', deliveryDate: null, trackingNumber: null, lineItems: [], totalAmount: 99.99
    }]))
  ),
};