export type Role = "MEMBER" | "ADMIN";

export interface User {
  id: number;
  username: string;
  email: string;
  memberId: string;
  fullName: string;
  role?: Role;
  createdAt: string;
  updatedAt: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  username: string;
  memberId: string;
  role: Role;
  expirationTime: number;
}

export interface SignupRequest {
  username: string;
  email: string;
  password: string;
  fullName: string;
}

/** Profile update. Omit password to keep it; members changing their own must send currentPassword. */
export interface UpdateUserRequest {
  username: string;
  email: string;
  fullName: string;
  memberId?: string;
  password?: string;
  currentPassword?: string;
}

export interface MessageResponse {
  message: string;
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
  refillFrequency: "WEEKLY" | "MONTHLY";
  memberLocation: string;
  subscriptionStatus: "ACTIVE" | "INACTIVE";
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
  refillFrequency: "WEEKLY" | "MONTHLY";
  memberLocation: string;
}

export interface RefillLineItemResponse {
  lineItemId: number;
  drugCode: string;
  drugName: string;
  quantity: number;
  prescriptionId: string;
  unitPrice: number;
  totalPrice: number;
}

export interface RefillStatusResponse {
  refillOrderId: string;
  memberId: string;
  orderDate: string;
  memberLocation: string;
  orderStatus:
    | "PENDING"
    | "CONFIRMED"
    | "IN_PROGRESS"
    | "SHIPPED"
    | "DELIVERED"
    | "CANCELLED";
  orderType: "SCHEDULED" | "ADHOC";
  subscriptionId: string | null;
  deliveryDate: string | null;
  trackingNumber: string | null;
  lineItems: RefillLineItemResponse[];
  totalAmount: number;
}

export interface AdhocRefillPrescriptionRequest {
  prescriptionId: string;
  drugCode: string;
  quantity: number;
}

export interface AdhocRefillRequest {
  memberId: string;
  memberLocation: string;
  prescriptions: AdhocRefillPrescriptionRequest[];
}

export interface AdhocRefillResponse {
  refillOrderId: string;
  memberId: string;
  orderDate: string;
  memberLocation: string;
  orderStatus: string;
  message: string;
  totalAmount: number;
}

export interface RefillDuePrescription {
  prescriptionId: string;
  drugId: string;
  dosagePerDay: string;
  prescriptionCourse: number;
}

export interface RefillDueResponse {
  subscriptionId: string;
  memberId: string;
  memberLocation: string;
  dueDate: string;
  refillFrequency: string;
  prescriptions: RefillDuePrescription[];
}
