-- Remove account_number column if it exists
ALTER TABLE Accounts DROP COLUMN IF EXISTS account_number;

-- Update existing accounts with generated account numbers
UPDATE Accounts SET account_number = LPAD(FLOOR(RANDOM() * 10000000000)::TEXT, 10, '0')
WHERE account_number IS NULL; 