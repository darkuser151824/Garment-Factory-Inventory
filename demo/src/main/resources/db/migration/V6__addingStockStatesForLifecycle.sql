-- V8__inventory_state_columns.sql

-- Step 1: Add five new quantity columns
ALTER TABLE stock ADD COLUMN available_qty INTEGER NOT NULL DEFAULT 0;
ALTER TABLE stock ADD COLUMN allocated_qty INTEGER NOT NULL DEFAULT 0;
ALTER TABLE stock ADD COLUMN in_production_qty INTEGER NOT NULL DEFAULT 0;
ALTER TABLE stock ADD COLUMN ready_qty INTEGER NOT NULL DEFAULT 0;
ALTER TABLE stock ADD COLUMN dispatched_qty INTEGER NOT NULL DEFAULT 0;

-- Step 2: Copy existing quantity into available_qty
UPDATE stock SET available_qty = quantity;

-- Step 3: Backfill allocated_qty from active orders
-- All 33 ORDERED orders have stock committed
UPDATE stock s
SET allocated_qty = COALESCE((
    SELECT SUM(oi.quantity)
    FROM order_item oi
    JOIN orders o ON oi.order_id = o.order_id
    WHERE oi.p_id = s.product_id
      AND oi.size = s.size
      AND o.status IN ('ORDERED', 'IN_PRODUCTION', 'READY')
      AND oi.is_deleted = false
      AND o.is_deleted = false
), 0);

-- Step 4: Backfill dispatched_qty from delivered orders
-- You have 0 delivered orders so this sets everything to 0
UPDATE stock s
SET dispatched_qty = COALESCE((
    SELECT SUM(oi.quantity)
    FROM order_item oi
    JOIN orders o ON oi.order_id = o.order_id
    WHERE oi.p_id = s.product_id
      AND oi.size = s.size
      AND o.status = 'DELIVERED'
      AND oi.is_deleted = false
      AND o.is_deleted = false
), 0);

-- Step 5: Drop the old quantity column
ALTER TABLE stock DROP COLUMN quantity;