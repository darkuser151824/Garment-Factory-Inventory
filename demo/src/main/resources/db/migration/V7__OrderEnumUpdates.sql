-- V9__update_orders_status_constraint.sql

-- Drop the old check constraint
ALTER TABLE orders DROP CONSTRAINT orders_status_check;

-- Add new constraint with all valid values
ALTER TABLE orders ADD CONSTRAINT orders_status_check
CHECK (status IN ('ORDERED', 'IN_PRODUCTION', 'READY', 'DELIVERED', 'CANCELLED'));