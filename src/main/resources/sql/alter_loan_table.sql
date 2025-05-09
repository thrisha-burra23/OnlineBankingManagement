-- Add remaining_balance and total_paid columns to loans table
ALTER TABLE loans
ADD COLUMN remaining_balance DECIMAL(15,2) DEFAULT 0.00,
ADD COLUMN total_paid DECIMAL(15,2) DEFAULT 0.00;

-- Update existing loans to set remaining_balance to loan_amount
UPDATE loans
SET remaining_balance = loan_amount
WHERE status = 'APPROVED'; 