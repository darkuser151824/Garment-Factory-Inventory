-- V12__add_invoice_generated_flag.sql

ALTER TABLE orders
ADD COLUMN is_invoice_generated BOOLEAN NOT NULL DEFAULT FALSE;