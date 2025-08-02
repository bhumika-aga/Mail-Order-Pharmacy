import axios, { AxiosResponse } from 'axios';
import {
  AdhocRefillRequest,
  AuthResponse,
  Drug,
  DrugStockResponse,
  MemberPrescription,
  MemberSubscription,
  RefillStatusResponse,
  SubscriptionRequest
} from '../types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost';

const authApi = axios.create({
  baseURL: `${API_BASE_URL}:8084/api/auth`,
});

const drugsApi = axios.create({
  baseURL: `${API_BASE_URL}:8081/api/drugs`,
});

const subscriptionApi = axios.create({
  baseURL: `${API_BASE_URL}:8082/api/subscriptions`,
});

const refillApi = axios.create({
  baseURL: `${API_BASE_URL}:8083/api/refill`,
});

const setAuthToken = (token: string) => {
  const bearerToken = `Bearer ${token}`;
  drugsApi.defaults.headers.common['Authorization'] = bearerToken;
  subscriptionApi.defaults.headers.common['Authorization'] = bearerToken;
  refillApi.defaults.headers.common['Authorization'] = bearerToken;
};

const clearAuthToken = () => {
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
  getRefillStatus: (): Promise<AxiosResponse<RefillStatusResponse[]>> =>
    refillApi.get('/viewRefillStatus'),

  getRefillDues: (date: string): Promise<AxiosResponse<any[]>> =>
    refillApi.get(`/getRefillDuesAsOfDate?date=${date}`),

  requestAdhocRefill: (request: AdhocRefillRequest): Promise<AxiosResponse<any>> =>
    refillApi.post('/requestAdhocRefill', request),

  getOrders: (): Promise<AxiosResponse<any[]>> =>
    refillApi.get('/orders'),
};

export { clearAuthToken, setAuthToken };
