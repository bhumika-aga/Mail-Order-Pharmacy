import {
  AuthResponse,
  Drug,
  MemberSubscription,
  MessageResponse,
  RefillStatusResponse,
  SignupRequest,
  SubscriptionRequest,
  User
} from '../index';

describe('Type Definitions', () => {
  it('should have correct User interface', () => {
    const user: User = {
      id: 1,
      username: 'testuser',
      email: 'test@example.com',
      memberId: 'MEM123456',
      fullName: 'Test User',
      createdAt: '2023-01-01T00:00:00Z',
      updatedAt: '2023-01-01T00:00:00Z',
    };

    expect(user.id).toBe(1);
    expect(user.username).toBe('testuser');
    expect(user.email).toBe('test@example.com');
    expect(user.memberId).toBe('MEM123456');
    expect(user.fullName).toBe('Test User');
  });

  it('should have correct AuthResponse interface', () => {
    const authResponse: AuthResponse = {
      token: 'jwt-token',
      type: 'Bearer',
      username: 'testuser',
      memberId: 'MEM123456',
      expirationTime: 900000,
    };

    expect(authResponse.token).toBe('jwt-token');
    expect(authResponse.type).toBe('Bearer');
    expect(authResponse.username).toBe('testuser');
    expect(authResponse.memberId).toBe('MEM123456');
    expect(authResponse.expirationTime).toBe(900000);
  });

  it('should have correct SignupRequest interface', () => {
    const signupRequest: SignupRequest = {
      username: 'newuser',
      email: 'new@example.com',
      password: 'password123',
      fullName: 'New User',
      memberId: 'MEM789012',
    };

    expect(signupRequest.username).toBe('newuser');
    expect(signupRequest.email).toBe('new@example.com');
    expect(signupRequest.password).toBe('password123');
    expect(signupRequest.fullName).toBe('New User');
    expect(signupRequest.memberId).toBe('MEM789012');
  });

  it('should have correct MessageResponse interface', () => {
    const messageResponse: MessageResponse = {
      message: 'Success',
    };

    expect(messageResponse.message).toBe('Success');
  });

  it('should have correct Drug interface', () => {
    const drug: Drug = {
      drugId: 'DRUG001',
      drugName: 'Aspirin',
      manufacturer: 'PharmaCorp',
      manufacturedDate: '2023-01-01',
      expiryDate: '2025-01-01',
      unitsPerPackage: 30,
      costPerPackage: 15.99,
      medicalComposition: 'Acetylsalicylic acid',
    };

    expect(drug.drugId).toBe('DRUG001');
    expect(drug.drugName).toBe('Aspirin');
    expect(drug.manufacturer).toBe('PharmaCorp');
    expect(drug.unitsPerPackage).toBe(30);
    expect(drug.costPerPackage).toBe(15.99);
  });

  it('should have correct RefillStatusResponse interface with orderStatus', () => {
    const refillStatus: RefillStatusResponse = {
      refillOrderId: 'ORDER001',
      memberId: 'MEM123456',
      orderDate: '2023-01-01',
      memberLocation: 'New York',
      orderStatus: 'PENDING',
      orderType: 'SCHEDULED',
      subscriptionId: 'SUB001',
      deliveryDate: null,
      trackingNumber: null,
      lineItems: [],
      totalAmount: 99.99,
    };

    expect(refillStatus.orderStatus).toBe('PENDING');
    expect(refillStatus.orderType).toBe('SCHEDULED');
    expect(refillStatus.totalAmount).toBe(99.99);
  });

  it('should have correct MemberSubscription interface', () => {
    const subscription: MemberSubscription = {
      subscriptionId: 'SUB001',
      memberId: 'MEM123456',
      subscriptionDate: '2023-01-01',
      prescriptionId: 'PRESC001',
      refillFrequency: 'MONTHLY',
      memberLocation: 'New York',
      subscriptionStatus: 'ACTIVE',
    };

    expect(subscription.refillFrequency).toBe('MONTHLY');
    expect(subscription.subscriptionStatus).toBe('ACTIVE');
  });

  it('should have correct SubscriptionRequest interface', () => {
    const request: SubscriptionRequest = {
      insurancePolicyNumber: 'INS123456',
      insuranceProvider: 'HealthInsurance Co',
      prescriptionDate: '2023-01-01',
      drugId: 'DRUG001',
      dosagePerDay: '1 tablet',
      prescriptionCourse: 30,
      doctorDetails: 'Dr. Smith',
      refillFrequency: 'MONTHLY',
      memberLocation: 'New York',
    };

    expect(request.insurancePolicyNumber).toBe('INS123456');
    expect(request.refillFrequency).toBe('MONTHLY');
    expect(request.prescriptionCourse).toBe(30);
  });
});