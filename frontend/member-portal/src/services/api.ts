import axios, { AxiosResponse } from "axios";
import {
  AdhocRefillRequest,
  AdhocRefillResponse,
  AuthResponse,
  Drug,
  DrugLocation,
  DrugStockResponse,
  MemberPrescription,
  MemberSubscription,
  MessageResponse,
  RefillDueResponse,
  RefillStatusResponse,
  SignupRequest,
  SubscriptionRequest,
  UpdateUserRequest,
  User,
} from "../types";

const AUTH_SERVICE_URL =
  import.meta.env.VITE_AUTH_SERVICE_URL || "http://localhost:8084";
const DRUGS_SERVICE_URL =
  import.meta.env.VITE_DRUGS_SERVICE_URL || "http://localhost:8081";
const SUBSCRIPTION_SERVICE_URL =
  import.meta.env.VITE_SUBSCRIPTION_SERVICE_URL || "http://localhost:8082";
const REFILL_SERVICE_URL =
  import.meta.env.VITE_REFILL_SERVICE_URL || "http://localhost:8083";

const authApi = axios.create({
  baseURL: `${AUTH_SERVICE_URL}/api/auth`,
});

const drugsApi = axios.create({
  baseURL: `${DRUGS_SERVICE_URL}/api/drugs`,
});

const subscriptionApi = axios.create({
  baseURL: `${SUBSCRIPTION_SERVICE_URL}/api/subscriptions`,
});

const refillApi = axios.create({
  baseURL: `${REFILL_SERVICE_URL}/api/refill`,
});

const apiClients = [authApi, drugsApi, subscriptionApi, refillApi];

export const setAuthToken = (token: string) => {
  const bearerToken = `Bearer ${token}`;
  apiClients.forEach((client) => {
    client.defaults.headers.common["Authorization"] = bearerToken;
  });
};

export const clearAuthToken = () => {
  apiClients.forEach((client) => {
    delete client.defaults.headers.common["Authorization"];
  });
};

// Every API now requires a valid JWT. When the session expires (401), hand off to the
// registered handler (the auth provider logs the user out) instead of leaving pages
// silently failing. Sign-in and token validation handle their own 401s.
let unauthorizedHandler: (() => void) | null = null;

export const setUnauthorizedHandler = (handler: (() => void) | null) => {
  unauthorizedHandler = handler;
};

apiClients.forEach((client) => {
  client.interceptors.response.use(undefined, (error) => {
    const url: string = error?.config?.url ?? "";
    const isAuthCall = url.includes("/signin") || url.includes("/validate");
    if (error?.response?.status === 401 && !isAuthCall) {
      unauthorizedHandler?.();
    }
    return Promise.reject(error);
  });
});

export const authService = {
  login: (
    username: string,
    password: string,
  ): Promise<AxiosResponse<AuthResponse>> =>
    authApi.post("/signin", { username, password }),

  validateToken: (token: string): Promise<AxiosResponse<AuthResponse>> =>
    authApi.post(
      "/validate",
      {},
      {
        headers: { Authorization: `Bearer ${token}` },
      },
    ),

  register: (
    signupData: SignupRequest,
  ): Promise<AxiosResponse<MessageResponse>> =>
    authApi.post("/signup", signupData),

  getAllUsers: (): Promise<AxiosResponse<User[]>> => authApi.get("/users"),

  getUserById: (id: number): Promise<AxiosResponse<User>> =>
    authApi.get(`/users/${encodeURIComponent(id)}`),

  updateUser: (
    id: number,
    userData: UpdateUserRequest,
  ): Promise<AxiosResponse<User>> =>
    authApi.put(`/users/${encodeURIComponent(id)}`, userData),

  deleteUser: (id: number): Promise<AxiosResponse<MessageResponse>> =>
    authApi.delete(`/users/${encodeURIComponent(id)}`),

  checkUsernameAvailability: (
    username: string,
  ): Promise<AxiosResponse<MessageResponse>> =>
    authApi.get(`/check-username/${encodeURIComponent(username)}`),

  checkEmailAvailability: (
    email: string,
  ): Promise<AxiosResponse<MessageResponse>> =>
    authApi.get(`/check-email/${encodeURIComponent(email)}`),

  checkMemberIdAvailability: (
    memberId: string,
  ): Promise<AxiosResponse<MessageResponse>> =>
    authApi.get(`/check-memberid/${encodeURIComponent(memberId)}`),
};

export const drugService = {
  searchById: (drugId: string): Promise<AxiosResponse<Drug>> =>
    drugsApi.get("/searchDrugsByID", { params: { drugId } }),

  searchByName: (drugName: string): Promise<AxiosResponse<Drug[]>> =>
    drugsApi.get("/searchDrugsByName", { params: { drugName } }),

  getDispatchableStock: (
    drugIds: string[],
    location: string,
  ): Promise<AxiosResponse<DrugStockResponse[]>> =>
    drugsApi.post("/getDispatchableDrugStock", { drugIds, location }),

  getAllValidDrugs: (): Promise<AxiosResponse<Drug[]>> => drugsApi.get("/all"),

  getDrugLocations: (drugId: string): Promise<AxiosResponse<DrugLocation[]>> =>
    drugsApi.get(`/${encodeURIComponent(drugId)}/locations`),
};

export const subscriptionService = {
  subscribe: (
    request: SubscriptionRequest,
  ): Promise<AxiosResponse<MemberSubscription>> =>
    subscriptionApi.post("/subscribe", request),

  unsubscribe: (
    subscriptionId: string,
  ): Promise<AxiosResponse<MessageResponse>> =>
    subscriptionApi.post("/unsubscribe", null, { params: { subscriptionId } }),

  getMySubscriptions: (): Promise<AxiosResponse<MemberSubscription[]>> =>
    subscriptionApi.get("/my-subscriptions"),

  getActiveSubscriptions: (): Promise<AxiosResponse<MemberSubscription[]>> =>
    subscriptionApi.get("/active"),

  getPrescriptions: (): Promise<AxiosResponse<MemberPrescription[]>> =>
    subscriptionApi.get("/prescriptions"),

  getSubscriptionById: (
    subscriptionId: string,
  ): Promise<AxiosResponse<MemberSubscription>> =>
    subscriptionApi.get(`/${encodeURIComponent(subscriptionId)}`),
};

export const refillService = {
  getRefillStatus: (): Promise<AxiosResponse<RefillStatusResponse>> =>
    refillApi.get("/viewRefillStatus"),

  getAllRefillOrders: (): Promise<AxiosResponse<RefillStatusResponse[]>> =>
    refillApi.get("/orders"),

  getRefillDues: (date: string): Promise<AxiosResponse<RefillDueResponse[]>> =>
    refillApi.get("/getRefillDuesAsOfDate", { params: { date } }),

  requestAdhocRefill: (
    request: AdhocRefillRequest,
  ): Promise<AxiosResponse<AdhocRefillResponse>> =>
    refillApi.post("/requestAdhocRefill", request),
};
