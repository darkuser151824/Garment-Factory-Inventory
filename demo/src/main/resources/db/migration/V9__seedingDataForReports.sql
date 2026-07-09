-- V11__seed_data.sql
-- Seed data: new products, new users, 400+ orders across 12 months
-- Business cycle: low(3m) -> dip(2m) -> peak(6m) -> cooling(1m) -> growing(4m+current)
-- Password hash for VedaNsh@2737: $2a$10$FT.c2kC8stcEmXuknpz7J.P3YRA8X/vlLjgNRUOKqfS8ZcwTxNq4m

-- ─────────────────────────────────────────
-- NEW PRODUCTS (with stock)
-- ─────────────────────────────────────────
INSERT INTO product (garment, color, fabric, selling_price, cost_per_unit, created_at, updated_at, is_deleted)
VALUES
('KURTA',  'BLUE',   'COTTON',    650.00, 320.00, NOW(), NOW(), false),
('KURTA',  'WHITE',  'POLYESTER', 580.00, 280.00, NOW(), NOW(), false),
('JACKET', 'BLACK',  'RAYON',    1100.00, 620.00, NOW(), NOW(), false),
('JACKET', 'GREEN',  'SILK',     1350.00, 750.00, NOW(), NOW(), false),
('TSHIRT', 'GREEN',  'COTTON',    320.00, 160.00, NOW(), NOW(), false);

-- Stock for new products (we use subquery to get the new pids)
INSERT INTO stock (product_id, size, available_qty, allocated_qty, in_production_qty, ready_qty, dispatched_qty, created_at, updated_at, is_deleted, version)
SELECT pid, 'SMALL',  150, 0, 0, 0, 0, NOW(), NOW(), false, 0 FROM product WHERE garment = 'KURTA'  AND color = 'BLUE'  AND fabric = 'COTTON'
UNION ALL
SELECT pid, 'MEDIUM', 150, 0, 0, 0, 0, NOW(), NOW(), false, 0 FROM product WHERE garment = 'KURTA'  AND color = 'BLUE'  AND fabric = 'COTTON'
UNION ALL
SELECT pid, 'LARGE',  150, 0, 0, 0, 0, NOW(), NOW(), false, 0 FROM product WHERE garment = 'KURTA'  AND color = 'BLUE'  AND fabric = 'COTTON'
UNION ALL
SELECT pid, 'SMALL',  150, 0, 0, 0, 0, NOW(), NOW(), false, 0 FROM product WHERE garment = 'KURTA'  AND color = 'WHITE' AND fabric = 'POLYESTER'
UNION ALL
SELECT pid, 'MEDIUM', 150, 0, 0, 0, 0, NOW(), NOW(), false, 0 FROM product WHERE garment = 'KURTA'  AND color = 'WHITE' AND fabric = 'POLYESTER'
UNION ALL
SELECT pid, 'LARGE',  150, 0, 0, 0, 0, NOW(), NOW(), false, 0 FROM product WHERE garment = 'KURTA'  AND color = 'WHITE' AND fabric = 'POLYESTER'
UNION ALL
SELECT pid, 'SMALL',  120, 0, 0, 0, 0, NOW(), NOW(), false, 0 FROM product WHERE garment = 'JACKET' AND color = 'BLACK' AND fabric = 'RAYON'
UNION ALL
SELECT pid, 'MEDIUM', 120, 0, 0, 0, 0, NOW(), NOW(), false, 0 FROM product WHERE garment = 'JACKET' AND color = 'BLACK' AND fabric = 'RAYON'
UNION ALL
SELECT pid, 'LARGE',  120, 0, 0, 0, 0, NOW(), NOW(), false, 0 FROM product WHERE garment = 'JACKET' AND color = 'BLACK' AND fabric = 'RAYON'
UNION ALL
SELECT pid, 'SMALL',  100, 0, 0, 0, 0, NOW(), NOW(), false, 0 FROM product WHERE garment = 'JACKET' AND color = 'GREEN' AND fabric = 'SILK'
UNION ALL
SELECT pid, 'MEDIUM', 100, 0, 0, 0, 0, NOW(), NOW(), false, 0 FROM product WHERE garment = 'JACKET' AND color = 'GREEN' AND fabric = 'SILK'
UNION ALL
SELECT pid, 'LARGE',  100, 0, 0, 0, 0, NOW(), NOW(), false, 0 FROM product WHERE garment = 'JACKET' AND color = 'GREEN' AND fabric = 'SILK'
UNION ALL
SELECT pid, 'SMALL',  200, 0, 0, 0, 0, NOW(), NOW(), false, 0 FROM product WHERE garment = 'TSHIRT' AND color = 'GREEN' AND fabric = 'COTTON'
UNION ALL
SELECT pid, 'MEDIUM', 200, 0, 0, 0, 0, NOW(), NOW(), false, 0 FROM product WHERE garment = 'TSHIRT' AND color = 'GREEN' AND fabric = 'COTTON'
UNION ALL
SELECT pid, 'LARGE',  200, 0, 0, 0, 0, NOW(), NOW(), false, 0 FROM product WHERE garment = 'TSHIRT' AND color = 'GREEN' AND fabric = 'COTTON';

-- ─────────────────────────────────────────
-- NEW USERS (password = VedaNsh@2737)
-- ─────────────────────────────────────────
INSERT INTO users (username, password, role, created_at, updated_at, is_deleted) VALUES
('RahulBuyer',   '$2a$10$FT.c2kC8stcEmXuknpz7J.P3YRA8X/vlLjgNRUOKqfS8ZcwTxNq4m', 'USER', NOW(), NOW(), false),
('PriyaSharma',  '$2a$10$FT.c2kC8stcEmXuknpz7J.P3YRA8X/vlLjgNRUOKqfS8ZcwTxNq4m', 'USER', NOW(), NOW(), false),
('AmitTrader',   '$2a$10$FT.c2kC8stcEmXuknpz7J.P3YRA8X/vlLjgNRUOKqfS8ZcwTxNq4m', 'USER', NOW(), NOW(), false),
('SuneetaWholesale', '$2a$10$FT.c2kC8stcEmXuknpz7J.P3YRA8X/vlLjgNRUOKqfS8ZcwTxNq4m', 'USER', NOW(), NOW(), false),
('ManishRetail', '$2a$10$FT.c2kC8stcEmXuknpz7J.P3YRA8X/vlLjgNRUOKqfS8ZcwTxNq4m', 'WORKER', NOW(), NOW(), false);

-- ─────────────────────────────────────────
-- SEED ORDERS + ORDER ITEMS
-- Business cycle:
--   Month -12 to -10: LOW   ~20/month  (Jul-Sep 2025)
--   Month -9  to -8:  DIP   ~10/month  (Oct-Nov 2025)
--   Month -7  to -2:  PEAK  ~50/month  (Dec 2025 - May 2026)
--   Month -1:         COOL  ~20/month  (Jun 2026)
--   Current month:    GROW  ~30/month  (Jul 2026)
-- Status mix: 50% DELIVERED, 35% CANCELLED, 15% ORDERED (recent only)
-- All historical (>1 month ago) = DELIVERED or CANCELLED
-- Recent (current month) = ORDERED, IN_PRODUCTION, DELIVERED mix
-- ─────────────────────────────────────────

-- Helper: we use fixed user_ids from seed users
-- RahulBuyer, PriyaSharma, AmitTrader, SuneetaWholesale, ManishRetail
-- We'll use a DO block to generate orders programmatically

DO $$
DECLARE
    v_user_ids BIGINT[];
    v_product_ids BIGINT[];
    v_order_id BIGINT;
    v_user_id BIGINT;
    v_p1 BIGINT;
    v_p2 BIGINT;
    v_status VARCHAR;
    v_created_at TIMESTAMP;
    v_qty1 INT;
    v_qty2 INT;
    v_price1 NUMERIC;
    v_price2 NUMERIC;
    v_cost1 NUMERIC;
    v_cost2 NUMERIC;
    v_sizes VARCHAR[] := ARRAY['SMALL','MEDIUM','LARGE'];
    v_size1 VARCHAR;
    v_size2 VARCHAR;
    i INT;
    v_month_offset INT;
    v_orders_this_month INT;
    v_day INT;
    v_rand FLOAT;
BEGIN
    -- Get user IDs for seed users
    SELECT ARRAY(SELECT user_id FROM users WHERE username IN ('RahulBuyer','PriyaSharma','AmitTrader','SuneetaWholesale','ManishRetail') ORDER BY user_id)
    INTO v_user_ids;

    -- Get all product IDs
    SELECT ARRAY(SELECT pid FROM product WHERE is_deleted = false ORDER BY pid)
    INTO v_product_ids;

    -- Month offsets and order counts per month
    -- Negative = months ago from now
    FOR v_month_offset, v_orders_this_month IN
        SELECT * FROM (VALUES
            (-12, 20), (-11, 18), (-10, 22),  -- LOW
            (-9,  10), (-8,  12),              -- DIP
            (-7,  45), (-6,  52), (-5,  55), (-4, 50), (-3, 48), (-2, 50),  -- PEAK
            (-1,  20),                         -- COOLING
            (0,   30)                          -- GROWING (current month)
        ) AS t(mo, cnt)
    LOOP
        FOR i IN 1..v_orders_this_month LOOP
            -- Random day within the month
            v_day := 1 + (i % 28);

            -- Calculate created_at timestamp
            v_created_at := DATE_TRUNC('month', NOW()) + (v_month_offset || ' months')::INTERVAL
                           + ((v_day - 1) || ' days')::INTERVAL
                           + (FLOOR(RANDOM() * 8 + 8) || ' hours')::INTERVAL;

            -- Random user from seed users
            v_user_id := v_user_ids[1 + (FLOOR(RANDOM() * ARRAY_LENGTH(v_user_ids, 1)))::INT % ARRAY_LENGTH(v_user_ids, 1)];

            -- Random two different products
            v_p1 := v_product_ids[1 + (FLOOR(RANDOM() * ARRAY_LENGTH(v_product_ids, 1)))::INT % ARRAY_LENGTH(v_product_ids, 1)];
            v_p2 := v_product_ids[1 + (FLOOR(RANDOM() * ARRAY_LENGTH(v_product_ids, 1)))::INT % ARRAY_LENGTH(v_product_ids, 1)];

            -- Random sizes
            v_size1 := v_sizes[1 + (FLOOR(RANDOM() * 3))::INT % 3];
            v_size2 := v_sizes[1 + (FLOOR(RANDOM() * 3))::INT % 3];

            -- Random quantities 1-3
            v_qty1 := 1 + (FLOOR(RANDOM() * 3))::INT;
            v_qty2 := 1 + (FLOOR(RANDOM() * 3))::INT;

            -- Get prices for chosen products
            SELECT selling_price, cost_per_unit INTO v_price1, v_cost1 FROM product WHERE pid = v_p1;
            SELECT selling_price, cost_per_unit INTO v_price2, v_cost2 FROM product WHERE pid = v_p2;

            -- Determine status based on month and random
            v_rand := RANDOM();
            IF v_month_offset = 0 THEN
                -- Current month: mix of active statuses
                IF v_rand < 0.40 THEN v_status := 'DELIVERED';
                ELSIF v_rand < 0.65 THEN v_status := 'ORDERED';
                ELSIF v_rand < 0.80 THEN v_status := 'IN_PRODUCTION';
                ELSIF v_rand < 0.90 THEN v_status := 'READY';
                ELSE v_status := 'CANCELLED';
                END IF;
            ELSE
                -- Historical: 50% DELIVERED, 35% CANCELLED, 15% ORDERED
                IF v_rand < 0.50 THEN v_status := 'DELIVERED';
                ELSIF v_rand < 0.85 THEN v_status := 'CANCELLED';
                ELSE v_status := 'ORDERED';
                END IF;
            END IF;

            -- Insert order
            INSERT INTO orders (status, total_amount, total_cost, total_profit, created_at, updated_at, is_deleted, user_id)
            VALUES (
                v_status,
                (v_price1 * v_qty1) + (v_price2 * v_qty2),
                (v_cost1  * v_qty1) + (v_cost2  * v_qty2),
                ((v_price1 - v_cost1) * v_qty1) + ((v_price2 - v_cost2) * v_qty2),
                v_created_at,
                v_created_at + INTERVAL '1 hour',
                false,
                v_user_id
            ) RETURNING order_id INTO v_order_id;

            -- Insert order items
            INSERT INTO order_item (order_id, p_id, quantity, size, price_at_purchase, total_amount_of_item, total_cost_of_item, total_profit_of_item, created_at, updated_at, is_deleted)
            VALUES (
                v_order_id, v_p1, v_qty1, v_size1,
                v_price1, v_price1 * v_qty1, v_cost1 * v_qty1, (v_price1 - v_cost1) * v_qty1,
                v_created_at, v_created_at, false
            );

            INSERT INTO order_item (order_id, p_id, quantity, size, price_at_purchase, total_amount_of_item, total_cost_of_item, total_profit_of_item, created_at, updated_at, is_deleted)
            VALUES (
                v_order_id, v_p2, v_qty2, v_size2,
                v_price2, v_price2 * v_qty2, v_cost2 * v_qty2, (v_price2 - v_cost2) * v_qty2,
                v_created_at, v_created_at, false
            );

        END LOOP;
    END LOOP;
END $$;