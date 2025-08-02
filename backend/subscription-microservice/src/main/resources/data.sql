-- Insert member prescriptions
INSERT INTO member_prescriptions (prescription_id, member_id, insurance_policy_number, insurance_provider, prescription_date, drug_id, dosage_per_day, prescription_course, doctor_details) VALUES
('PRES001', 'MEM001', 'POL12345', 'BlueCross BlueShield', '2024-01-10', 'D001', '1 tablet twice daily', 30, 'Dr. John Smith, MD - Cardiologist, General Hospital, Phone: (555) 123-4567'),
('PRES002', 'MEM001', 'POL12345', 'BlueCross BlueShield', '2024-01-12', 'D002', '1 tablet daily with meals', 90, 'Dr. Sarah Johnson, MD - Endocrinologist, Diabetes Center, Phone: (555) 234-5678'),
('PRES003', 'MEM002', 'POL67890', 'Aetna Health', '2024-01-15', 'D003', '1 tablet daily', 30, 'Dr. Michael Brown, MD - Internal Medicine, City Clinic, Phone: (555) 345-6789'),
('PRES004', 'MEM002', 'POL67890', 'Aetna Health', '2024-01-18', 'D004', '1 tablet daily', 30, 'Dr. Emily Davis, MD - Family Medicine, Health Center, Phone: (555) 456-7890'),
('PRES005', 'MEM003', 'POL11111', 'Cigna Healthcare', '2024-01-20', 'D005', '1 tablet daily in morning', 60, 'Dr. Robert Wilson, MD - General Practice, Community Hospital, Phone: (555) 567-8901');

-- Insert member subscriptions
INSERT INTO member_subscriptions (subscription_id, member_id, subscription_date, prescription_id, refill_frequency, member_location, subscription_status) VALUES
('SUB001', 'MEM001', '2024-01-15', 'PRES001', 'MONTHLY', 'NYC', 'ACTIVE'),
('SUB002', 'MEM001', '2024-01-16', 'PRES002', 'MONTHLY', 'NYC', 'ACTIVE'),
('SUB003', 'MEM002', '2024-01-20', 'PRES003', 'WEEKLY', 'LAX', 'ACTIVE'),
('SUB004', 'MEM002', '2024-01-22', 'PRES004', 'MONTHLY', 'LAX', 'INACTIVE'),
('SUB005', 'MEM003', '2024-01-25', 'PRES005', 'MONTHLY', 'CHI', 'ACTIVE');