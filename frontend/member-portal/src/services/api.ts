import axios, { AxiosResponse } from 'axios';
import {
  AdhocRefillRequest,
  AuthResponse,
  Drug,
  DrugStockResponse,
  MemberPrescription,
  MemberSubscription,
  MessageResponse,
  RefillDueResponse,
  RefillStatusResponse,
  SignupRequest,
  SubscriptionRequest,
  User
} from '../types';

const AUTH_SERVICE_URL = import.meta.env.VITE_AUTH_SERVICE_URL || 'http://localhost:8084';
const DRUGS_SERVICE_URL = import.meta.env.VITE_DRUGS_SERVICE_URL || 'http://localhost:8081';
const SUBSCRIPTION_SERVICE_URL = import.meta.env.VITE_SUBSCRIPTION_SERVICE_URL || 'http://localhost:8082';
const REFILL_SERVICE_URL = import.meta.env.VITE_REFILL_SERVICE_URL || 'http://localhost:8083';

console.log('AUTH_SERVICE_URL:', AUTH_SERVICE_URL);

const authApi = axios.create({
  baseURL: `${AUTH_SERVICE_URL}/api/auth`,
});

console.log('Auth API baseURL:', authApi.defaults.baseURL);

const drugsApi = axios.create({
  baseURL: `${DRUGS_SERVICE_URL}/api/drugs`,
});

const subscriptionApi = axios.create({
  baseURL: `${SUBSCRIPTION_SERVICE_URL}/api/subscriptions`,
});

const refillApi = axios.create({
  baseURL: `${REFILL_SERVICE_URL}/api/refill`,
});

export const setAuthToken = (token: string) => {
  const bearerToken = `Bearer ${token}`;
  drugsApi.defaults.headers.common['Authorization'] = bearerToken;
  subscriptionApi.defaults.headers.common['Authorization'] = bearerToken;
  refillApi.defaults.headers.common['Authorization'] = bearerToken;
};

export const clearAuthToken = () => {
  delete drugsApi.defaults.headers.common['Authorization'];
  delete subscriptionApi.defaults.headers.common['Authorization'];
  delete refillApi.defaults.headers.common['Authorization'];
};

export const authService = {
  login: (username: string, password: string): Promise<AxiosResponse<AuthResponse>> =>
    authApi.post('/signin', { username, password }),

  validateToken: (token: string): Promise<AxiosResponse<AuthResponse>> =>
    authApi.post('/validate', {}, {
      headers: { Authorization: `Bearer ${token}` }
    }),

  register: (signupData: SignupRequest): Promise<AxiosResponse<MessageResponse>> => {
    console.log('Registering user with data:', signupData);
    console.log('Using URL:', `${authApi.defaults.baseURL}/signup`);
    return authApi.post('/signup', signupData);
  },

  getAllUsers: (): Promise<AxiosResponse<User[]>> =>
    authApi.get('/users'),

  getUserById: (id: number): Promise<AxiosResponse<User>> =>
    authApi.get(`/users/${id}`),

  updateUser: (id: number, userData: SignupRequest): Promise<AxiosResponse<User>> =>
    authApi.put(`/users/${id}`, userData),

  deleteUser: (id: number): Promise<AxiosResponse<MessageResponse>> =>
    authApi.delete(`/users/${id}`),

  checkUsernameAvailability: (username: string): Promise<AxiosResponse<MessageResponse>> =>
    authApi.get(`/check-username/${username}`),

  checkEmailAvailability: (email: string): Promise<AxiosResponse<MessageResponse>> =>
    authApi.get(`/check-email/${email}`),

  checkMemberIdAvailability: (memberId: string): Promise<AxiosResponse<MessageResponse>> =>
    authApi.get(`/check-memberid/${memberId}`),
};

export const drugService = {
  searchById: (drugId: string): Promise<AxiosResponse<Drug>> =>
    drugsApi.get(`/searchDrugsByID?drugId=${drugId}`),

  searchByName: (drugName: string): Promise<AxiosResponse<Drug[]>> =>
    drugsApi.get(`/searchDrugsByName?drugName=${drugName}`),

  getDispatchableStock: (drugIds: string[], location: string): Promise<AxiosResponse<DrugStockResponse[]>> =>
    drugsApi.post('/getDispatchableDrugStock', { drugIds, location }),

  getAllValidDrugs: (): Promise<AxiosResponse<Drug[]>> =>
    drugsApi.get('/all'),

  getDrugLocations: (drugId: string): Promise<AxiosResponse<any[]>> =>
    drugsApi.get(`/${drugId}/locations`),
};

export const subscriptionService = {
  subscribe: (request: SubscriptionRequest): Promise<AxiosResponse<MemberSubscription>> =>
    subscriptionApi.post('/subscribe', request),

  unsubscribe: (subscriptionId: string): Promise<AxiosResponse<any>> =>
    subscriptionApi.post(`/unsubscribe?subscriptionId=${subscriptionId}`),

  getMySubscriptions: (): Promise<AxiosResponse<MemberSubscription[]>> =>
    subscriptionApi.get('/my-subscriptions'),

  getActiveSubscriptions: (): Promise<AxiosResponse<MemberSubscription[]>> =>
    subscriptionApi.get('/active'),

  getPrescriptions: (): Promise<AxiosResponse<MemberPrescription[]>> =>
    subscriptionApi.get('/prescriptions'),

  getSubscriptionById: (subscriptionId: string): Promise<AxiosResponse<MemberSubscription>> =>
    subscriptionApi.get(`/${subscriptionId}`),
};

export const refillService = {
  getRefillStatus: (): Promise<AxiosResponse<RefillStatusResponse>> =>
    refillApi.get('/viewRefillStatus'),

  getAllRefillOrders: (): Promise<AxiosResponse<RefillStatusResponse[]>> =>
    refillApi.get('/orders'),

  getRefillDues: (date: string): Promise<AxiosResponse<RefillDueResponse[]>> =>
    refillApi.get(`/getRefillDuesAsOfDate?date=${date}`),

  requestAdhocRefill: (request: AdhocRefillRequest): Promise<AxiosResponse<any>> =>
    refillApi.post('/requestAdhocRefill', request),

  getOrders: (): Promise<AxiosResponse<any[]>> =>
    refillApi.get('/orders'),
};

