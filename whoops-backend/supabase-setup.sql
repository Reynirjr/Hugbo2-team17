-- Run this entire script in Supabase Dashboard → SQL Editor → New query, then Run.
-- Creates tables, enables read access for the app (anon), and seeds Haga-vagninn menu.

-- 1. Tables (same as seed.sql)
CREATE TABLE IF NOT EXISTS menus (
  id BIGSERIAL PRIMARY KEY,
  name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS sections (
  id BIGSERIAL PRIMARY KEY,
  menu_id BIGINT NOT NULL REFERENCES menus(id) ON DELETE CASCADE,
  name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
  id BIGSERIAL PRIMARY KEY,
  section_id BIGINT NOT NULL REFERENCES sections(id) ON DELETE CASCADE,
  name TEXT NOT NULL,
  description TEXT NOT NULL,
  price_isk INTEGER NOT NULL,
  available BOOLEAN NOT NULL DEFAULT true,
  tags TEXT NOT NULL DEFAULT ''
);

-- 2. Row Level Security: allow anonymous read for menu data
ALTER TABLE menus ENABLE ROW LEVEL SECURITY;
ALTER TABLE sections ENABLE ROW LEVEL SECURITY;
ALTER TABLE items ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Allow anon read menus" ON menus;
DROP POLICY IF EXISTS "Allow anon read sections" ON sections;
DROP POLICY IF EXISTS "Allow anon read items" ON items;
CREATE POLICY "Allow anon read menus" ON menus FOR SELECT TO anon USING (true);
CREATE POLICY "Allow anon read sections" ON sections FOR SELECT TO anon USING (true);
CREATE POLICY "Allow anon read items" ON items FOR SELECT TO anon USING (true);

-- 3. Seed data (Haga-vagninn)
INSERT INTO menus (id, name) VALUES (1, 'Haga-vagninn – Kjöt eða Vegan')
ON CONFLICT (id) DO NOTHING;

INSERT INTO sections (id, menu_id, name) VALUES
  (1, 1, 'Máltíðir'),
  (2, 1, 'Borgarar'),
  (3, 1, 'Aukavörur'),
  (4, 1, 'Drykkir')
ON CONFLICT (id) DO NOTHING;

INSERT INTO items (section_id, name, description, price_isk, available, tags) VALUES
  (1, 'Double Börger', 'Börger með tvöföldu kjöti og osti, franskar & gos', 3290, true, 'meat,vegan'),
  (1, 'Börger', 'Börger, franskar & gos', 2690, true, 'meat,vegan'),
  (1, 'Fjölskyldu tilboð', '2x Börger, 2x barna börgerar, stórar franskar', 6990, true, 'meat,vegan'),
  (1, 'Barna börger', 'Barna börger, franskar & djús (fyrir 12 ára og yngri)', 1590, true, 'meat,vegan'),
  (2, 'Hagabörger einfaldur', 'Ostur, pikklaður laukur, pikklaðar gúrkur, mæjó & sinnep. Kjöt eða veganbuff.', 1690, true, 'meat,vegan'),
  (2, 'Hagabörger tvöfaldur', 'Ostur, pikklaður laukur, pikklaðar gúrkur, mæjó & sinnep. Kjöt eða veganbuff.', 2490, true, 'meat,vegan'),
  (2, 'Laukbörger einfaldur', 'Ostur, rautt relish, karmellíseraður laukur, mæjó & sinnep. Kjöt eða veganbuff.', 1690, true, 'meat,vegan'),
  (2, 'Laukbörger tvöfaldur', 'Ostur, rautt relish, karmellíseraður laukur, mæjó & sinnep. Kjöt eða veganbuff.', 2490, true, 'meat,vegan'),
  (2, 'Ostabörger einfaldur', 'Ostur, laukur, kál, tómatar, mæjó, sinnep & tómatSósa. Kjöt eða veganbuff.', 1690, true, 'meat,vegan'),
  (2, 'Ostabörger tvöfaldur', 'Ostur, laukur, kál, tómatar, mæjó, sinnep & tómatSósa. Kjöt eða veganbuff.', 2490, true, 'meat,vegan'),
  (2, 'Barnabörger', 'Kjöt eða veganbuff, ostur & tómatSósa', 1390, true, 'meat,vegan'),
  (3, 'Spæsí vegan vængir', 'Blómkál, tempura, chili, graslaukur & sriracha', 1490, true, 'vegan'),
  (3, 'Franskar lítill', 'Franskar', 790, true, 'vegan'),
  (3, 'Franskar stór', 'Stórar franskar', 1690, true, 'vegan'),
  (3, 'Spæsí franskar', 'Franskar með kryddi', 1490, true, 'vegan'),
  (3, 'Osta franskar', 'Franskar með osti', 1490, true, 'vegan'),
  (3, 'Sósur', 'Sósur á la carte', 390, true, ''),
  (4, 'Gos í dós', 'Coke, Coke Zero, Appelsín, Toppur', 390, true, 'vegan'),
  (4, 'Safi', 'Djús', 290, true, 'vegan');
