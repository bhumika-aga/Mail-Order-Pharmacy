-- ============================================================
-- Enable Row Level Security (RLS) on all public tables
-- Run this script once in the Supabase SQL Editor
-- ============================================================
-- The Spring Boot backend connects as the 'postgres' user
-- (the table owner), which bypasses RLS automatically.
-- This satisfies the Supabase database linter requirements
-- while keeping the backend fully functional.
-- ============================================================

-- 1. Users table (Auth Microservice)
ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;

-- 2. Drugs table (Drugs Microservice)
ALTER TABLE public.drugs ENABLE ROW LEVEL SECURITY;

-- 3. Drug Locations table (Drugs Microservice)
ALTER TABLE public.drug_locations ENABLE ROW LEVEL SECURITY;

-- 4. Member Prescriptions table (Subscription Microservice)
ALTER TABLE public.member_prescriptions ENABLE ROW LEVEL SECURITY;

-- 5. Member Subscriptions table (Subscription Microservice)
ALTER TABLE public.member_subscriptions ENABLE ROW LEVEL SECURITY;

-- 6. Refill Orders table (Refill Microservice)
ALTER TABLE public.refill_orders ENABLE ROW LEVEL SECURITY;

-- 7. Refill Order Line Items table (Refill Microservice)
ALTER TABLE public.refill_order_line_items ENABLE ROW LEVEL SECURITY;
