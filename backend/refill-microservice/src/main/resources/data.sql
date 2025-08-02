-- Insert refill orders
INSERT INTO refill_orders (refill_order_id, member_id, order_date, member_location, order_status, order_type,
                           subscription_id, delivery_date, tracking_number)
VALUES ('REF001', 'MEM001', '2024-01-20 10:30:00', 'NYC', 'DELIVERED', 'SCHEDULED', 'SUB001', '2024-01-22 14:00:00',
        'TRK001234567'),
       ('REF002', 'MEM001', '2024-02-20 09:15:00', 'NYC', 'SHIPPED', 'SCHEDULED', 'SUB002', '2024-02-23 16:30:00',
        'TRK001234568'),
       ('REF003', 'MEM002', '2024-01-25 11:45:00', 'LAX', 'IN_PROGRESS', 'SCHEDULED', 'SUB003', NULL, NULL),
       ('REF004', 'MEM002', '2024-02-01 14:20:00', 'LAX', 'PENDING', 'ADHOC', NULL, NULL, NULL),
       ('REF005', 'MEM003', '2024-02-05 08:30:00', 'CHI', 'CONFIRMED', 'SCHEDULED', 'SUB005', NULL, NULL),
       ('REF006', 'MEM001', '2024-02-10 16:00:00', 'NYC', 'PENDING', 'ADHOC', NULL, NULL, NULL);

-- Insert refill order line items
INSERT INTO refill_order_line_items (drug_code, drug_name, quantity, prescription_id, unit_price, total_price,
                                     refill_order_id)
VALUES ('D001', 'Lisinopril 10mg', 30, 'PRES001', 25.50, 765.00, 'REF001'),
       ('D002', 'Metformin 500mg', 90, 'PRES002', 15.75, 1417.50, 'REF002'),
       ('D003', 'Amlodipine 5mg', 30, 'PRES003', 22.00, 660.00, 'REF003'),
       ('D004', 'Simvastatin 20mg', 30, 'PRES004', 18.25, 547.50, 'REF004'),
       ('D005', 'Omeprazole 20mg', 60, 'PRES005', 12.50, 750.00, 'REF005'),
       ('D001', 'Lisinopril 10mg', 30, 'PRES001', 25.50, 765.00, 'REF006'),
       ('D002', 'Metformin 500mg', 30, 'PRES002', 15.75, 472.50, 'REF006');