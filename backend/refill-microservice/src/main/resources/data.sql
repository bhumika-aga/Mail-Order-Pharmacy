-- Sample data for Refill Microservice

-- Insert sample refill orders
INSERT INTO refill_orders (refill_order_id, member_id, order_date, member_location, order_status, order_type, subscription_id, delivery_date, tracking_number) VALUES
('RO001', 'MEM001', '2024-01-15 10:30:00', 'NYC', 'DELIVERED', 'SCHEDULED', 'SUB001', '2024-01-18 14:00:00', 'TRK12345'),
('RO002', 'MEM001', '2024-01-20 09:15:00', 'NYC', 'SHIPPED', 'ADHOC', NULL, NULL, 'TRK12346'),
('RO003', 'MEM002', '2024-01-22 11:45:00', 'LAX', 'IN_PROGRESS', 'SCHEDULED', 'SUB002', NULL, NULL),
('RO004', 'MEM003', '2024-01-25 16:20:00', 'CHI', 'CONFIRMED', 'ADHOC', NULL, NULL, NULL),
('RO005', 'MEM001', '2024-01-28 13:10:00', 'NYC', 'PENDING', 'SCHEDULED', 'SUB001', NULL, NULL);

-- Insert sample refill order line items
INSERT INTO refill_order_line_items (drug_code, drug_name, quantity, prescription_id, unit_price, total_price, refill_order_id) VALUES
-- For Order RO001
('D001', 'Lisinopril 10mg', 30, 'PRES001', 15.99, 479.70, 'RO001'),
('D002', 'Metformin 500mg', 60, 'PRES002', 12.50, 750.00, 'RO001'),

-- For Order RO002
('D003', 'Atorvastatin 20mg', 30, 'PRES003', 25.75, 772.50, 'RO002'),

-- For Order RO003
('D004', 'Amlodipine 5mg', 30, 'PRES004', 18.99, 569.70, 'RO003'),
('D005', 'Hydrochlorothiazide 25mg', 30, 'PRES005', 14.25, 427.50, 'RO003'),

-- For Order RO004
('D001', 'Lisinopril 10mg', 60, 'PRES006', 15.99, 959.40, 'RO004'),
('D006', 'Omeprazole 20mg', 30, 'PRES007', 22.99, 689.70, 'RO004'),

-- For Order RO005
('D002', 'Metformin 500mg', 90, 'PRES002', 12.50, 1125.00, 'RO005');