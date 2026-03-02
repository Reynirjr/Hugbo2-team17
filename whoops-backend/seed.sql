-- Haga-vagninn menu seed data for OOPS backend
-- Run against your PostgreSQL DB (e.g. psql $DATABASE_URL -f seed.sql)

-- Schema (if not already created)
CREATE TABLE IF NOT EXISTS menus (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS sections (
  id SERIAL PRIMARY KEY,
  menu_id INTEGER NOT NULL REFERENCES menus(id) ON DELETE CASCADE,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
  id SERIAL PRIMARY KEY,
  section_id INTEGER NOT NULL REFERENCES sections(id) ON DELETE CASCADE,
  name VARCHAR(255) NOT NULL,
  description TEXT NOT NULL,
  price_isk INTEGER NOT NULL,
  available BOOLEAN NOT NULL DEFAULT true,
  tags VARCHAR(255) NOT NULL DEFAULT ''
);

-- Clear existing data (optional; comment out if you want to keep other menus)
-- TRUNCATE items, sections, menus RESTART IDENTITY CASCADE;

-- Haga-vagninn menu
INSERT INTO menus (id, name) VALUES (1, 'Haga-vagninn – Kjöt eða Vegan')
ON CONFLICT (id) DO NOTHING;

-- Sections
INSERT INTO sections (id, menu_id, name) VALUES
  (1, 1, 'Máltíðir'),
  (2, 1, 'Borgarar'),
  (3, 1, 'Aukavörur'),
  (4, 1, 'Drykkir')
ON CONFLICT (id) DO NOTHING;

-- Máltíðir (Combo meals)
INSERT INTO items (section_id, name, description, price_isk, available, tags) VALUES
  (1, 'Double Börger', 'Börger með tvöföldu kjöti og osti, franskar & gos', 3290, true, 'meat,vegan'),
  (1, 'Börger', 'Börger, franskar & gos', 2690, true, 'meat,vegan'),
  (1, 'Fjölskyldu tilboð', '2x Börger, 2x barna börgerar, stórar franskar', 6990, true, 'meat,vegan'),
  (1, 'Barna börger', 'Barna börger, franskar & djús (fyrir 12 ára og yngri)', 1590, true, 'meat,vegan');

-- Borgarar (Burgers)
INSERT INTO items (section_id, name, description, price_isk, available, tags) VALUES
  (2, 'Hagabörger einfaldur', 'Ostur, pikklaður laukur, pikklaðar gúrkur, mæjó & sinnep. Kjöt eða veganbuff.', 1690, true, 'meat,vegan'),
  (2, 'Hagabörger tvöfaldur', 'Ostur, pikklaður laukur, pikklaðar gúrkur, mæjó & sinnep. Kjöt eða veganbuff.', 2490, true, 'meat,vegan'),
  (2, 'Laukbörger einfaldur', 'Ostur, rautt relish, karmellíseraður laukur, mæjó & sinnep. Kjöt eða veganbuff.', 1690, true, 'meat,vegan'),
  (2, 'Laukbörger tvöfaldur', 'Ostur, rautt relish, karmellíseraður laukur, mæjó & sinnep. Kjöt eða veganbuff.', 2490, true, 'meat,vegan'),
  (2, 'Ostabörger einfaldur', 'Ostur, laukur, kál, tómatar, mæjó, sinnep & tómatSósa. Kjöt eða veganbuff.', 1690, true, 'meat,vegan'),
  (2, 'Ostabörger tvöfaldur', 'Ostur, laukur, kál, tómatar, mæjó, sinnep & tómatSósa. Kjöt eða veganbuff.', 2490, true, 'meat,vegan'),
  (2, 'Barnabörger', 'Kjöt eða veganbuff, ostur & tómatSósa', 1390, true, 'meat,vegan');

-- Aukavörur (Sides)
INSERT INTO items (section_id, name, description, price_isk, available, tags) VALUES
  (3, 'Spæsí vegan vængir', 'Blómkál, tempura, chili, graslaukur & sriracha', 1490, true, 'vegan'),
  (3, 'Franskar lítill', 'Franskar', 790, true, 'vegan'),
  (3, 'Franskar stór', 'Stórar franskar', 1690, true, 'vegan'),
  (3, 'Spæsí franskar', 'Franskar með kryddi', 1490, true, 'vegan'),
  (3, 'Osta franskar', 'Franskar með osti', 1490, true, 'vegan'),
  (3, 'Sósur', 'Sósur á la carte', 390, true, '');

-- Drykkir (Drinks)
INSERT INTO items (section_id, name, description, price_isk, available, tags) VALUES
  (4, 'Gos í dós', 'Coke, Coke Zero, Appelsín, Toppur', 390, true, 'vegan'),
  (4, 'Safi', 'Djús', 290, true, 'vegan');
