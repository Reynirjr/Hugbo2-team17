-- Orders, order_items, restaurants, store_settings. Run after supabase-setup.sql.
-- Follows same structure as your old system for reference.

-- Restaurants: which venue + which menu (image per restaurant on Supabase)
CREATE TABLE IF NOT EXISTS restaurants (
  id BIGSERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  menu_id BIGINT NOT NULL REFERENCES menus(id) ON DELETE CASCADE,
  image_url TEXT
);

-- Store settings for queue/estimated time
CREATE TABLE IF NOT EXISTS store_settings (
  id SMALLINT PRIMARY KEY DEFAULT 1,
  queue_minutes INT NOT NULL DEFAULT 20,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Orders: track who (phone), which restaurant (menu_id), total, status, when ready
CREATE TABLE IF NOT EXISTS orders (
  id BIGSERIAL PRIMARY KEY,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  customer_phone TEXT NOT NULL,
  menu_id BIGINT NOT NULL REFERENCES menus(id) ON DELETE RESTRICT,
  status TEXT NOT NULL DEFAULT 'RECEIVED' CHECK (status IN ('RECEIVED','PREPARING','READY','PICKED_UP')),
  total_isk INT NOT NULL CHECK (total_isk >= 0),
  estimated_ready_at TIMESTAMPTZ
);

-- Order line items (snapshot of item name and price at order time)
CREATE TABLE IF NOT EXISTS order_items (
  id BIGSERIAL PRIMARY KEY,
  order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  item_id BIGINT NOT NULL REFERENCES items(id) ON DELETE RESTRICT,
  item_name TEXT NOT NULL,
  price_isk INT NOT NULL CHECK (price_isk >= 0),
  quantity INT NOT NULL CHECK (quantity > 0)
);

CREATE INDEX IF NOT EXISTS idx_orders_created_at ON orders (created_at);
CREATE INDEX IF NOT EXISTS idx_orders_customer_phone ON orders (customer_phone);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items (order_id);

-- RLS: anon can insert orders (place order) and read orders (e.g. by phone later)
ALTER TABLE restaurants ENABLE ROW LEVEL SECURITY;
ALTER TABLE orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE order_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE store_settings ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Allow anon read restaurants" ON restaurants;
CREATE POLICY "Allow anon read restaurants" ON restaurants FOR SELECT TO anon USING (true);

DROP POLICY IF EXISTS "Allow anon insert orders" ON orders;
CREATE POLICY "Allow anon insert orders" ON orders FOR INSERT TO anon WITH CHECK (true);
DROP POLICY IF EXISTS "Allow anon read orders" ON orders;
CREATE POLICY "Allow anon read orders" ON orders FOR SELECT TO anon USING (true);

DROP POLICY IF EXISTS "Allow anon insert order_items" ON order_items;
CREATE POLICY "Allow anon insert order_items" ON order_items FOR INSERT TO anon WITH CHECK (true);
DROP POLICY IF EXISTS "Allow anon read order_items" ON order_items;
CREATE POLICY "Allow anon read order_items" ON order_items FOR SELECT TO anon USING (true);

DROP POLICY IF EXISTS "Allow anon read store_settings" ON store_settings;
CREATE POLICY "Allow anon read store_settings" ON store_settings FOR SELECT TO anon USING (true);

-- Seed: one restaurant (Hagavagninn) using existing menu id 1
INSERT INTO restaurants (id, name, menu_id, image_url) VALUES
  (1, 'Haga-vagninn', 1, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO store_settings (id, queue_minutes) VALUES (1, 20)
ON CONFLICT (id) DO NOTHING;
