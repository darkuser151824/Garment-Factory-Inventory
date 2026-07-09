
ALTER TABLE product DROP CONSTRAINT product_garment_check;
ALTER TABLE product ADD CONSTRAINT product_garment_check
CHECK (garment IN ('SHIRT', 'TSHIRT', 'KURTA', 'JACKET'));

ALTER TABLE product DROP CONSTRAINT product_color_check;
ALTER TABLE product ADD CONSTRAINT product_color_check
CHECK (color IN ('RED', 'WHITE', 'BLACK', 'BLUE', 'ORANGE', 'GREEN'));

ALTER TABLE product DROP CONSTRAINT product_fabric_check;
ALTER TABLE product ADD CONSTRAINT product_fabric_check
CHECK (fabric IN ('RAYON', 'COTTON', 'POLYESTER', 'SILK'));