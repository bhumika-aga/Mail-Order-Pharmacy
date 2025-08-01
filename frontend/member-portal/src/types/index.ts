export interface User {
  id: string;
  username: string;
  email: string;
  memberId: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  username: string;
  memberId: string;
  expirationTime: number;
}

export interface Drug {
  drugId: string;
  drugName: string;
  manufacturer: string;
  manufacturedDate: string;
  expiryDate: string;
  unitsPerPackage: number;
  costPerPackage: number;
  medicalComposition: string;
}

export interface DrugLocation {
  drugId: string;
  location: string;
  quantityAvailable: number;
}

export interface DrugStockResponse {
  drugId: string;
  drugName: string;
  location: string;
  quantityAvailable: number;
  costPerPackage: number;
  isAvailable: boolean;
}

export interface MemberPrescription {
  prescriptionId: string;
  memberId: string;
  insurancePolicyNumber: string;
  insuranceProvider: string;
  prescriptionDate: string;
  drugId: string;
  dosagePerDay: string;
  prescriptionCourse: number;
  doctorDetails: string;
}

export interface MemberSubscription {
  subscriptionId: string;
  memberId: string;
  subscriptionDate: string;
  prescriptionId: string;
  refillFrequency: 'WEEKLY' | 'MONTHLY';
  memberLocation: string;
  subscriptionStatus: 'ACTIVE' | 'INACTIVE';
  prescription?: MemberPrescription;
}

export interface SubscriptionRequest {
  insurancePolicyNumber: string;
  insuranceProvider: string;
  prescriptionDate: string;
  drugId: string;
  dosagePerDay: string;
  prescriptionCourse: number;
  doctorDetails: string;
  refillFrequency: 'WEEKLY' | 'MONTHLY';
  memberLocation: string;
}

export interface RefillOrder {
  refillOrderId: string;
  subscriptionId: string;
  refillDate: string;
  quantity: number;
  paymentStatus: 'PAID' | 'PENDING';
}

export interface RefillOrderLineItem {
  refillOrderId: string;
  drugId: string;
  quantity: number;
}

export interface RefillStatusResponse {
  refillOrderId: string;
  subscriptionId: string;
  refillDate: string;
  quantity: number;
  paymentStatus: string;
  lineItems: RefillOrderLineItem[];
}

export interface AdhocRefillRequest {
  subscriptionId: string;
  quantity: number;
}