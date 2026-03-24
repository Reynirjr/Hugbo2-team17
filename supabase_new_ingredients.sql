-- New ingredients for sides customization
-- Run this in your Supabase SQL Editor

INSERT INTO ingredients (id, name, category, extra_price_isk, display_order) VALUES
  (21, 'Cheddar ostasósa', 'sosur', 0, 21),
  (22, 'Vorlaukur', 'alegg', 0, 22),
  (23, 'Pikklaður chili', 'alegg', 0, 23),
  (24, 'Hvitlauksmæjó', 'sosur', 0, 24),
  (25, 'Kokteilsósa', 'sosur', 0, 25);

-- Update item_ingredients defaults for sides with new ingredients

-- Spæsí vegan vængir (item 12): hvitlauksmæjó, siracha, vorlaukur, pikklaður chili
DELETE FROM item_ingredients WHERE item_id = 12;
INSERT INTO item_ingredients (item_id, ingredient_id) VALUES
  (12, 24),  -- Hvitlauksmæjó
  (12, 8),   -- Siracha
  (12, 22),  -- Vorlaukur
  (12, 23);  -- Pikklaður chili

-- Spæsí franskar (item 15): mæjó, siracha, vorlaukur, pikklaður chili
DELETE FROM item_ingredients WHERE item_id = 15;
INSERT INTO item_ingredients (item_id, ingredient_id) VALUES
  (15, 5),   -- Mæjó
  (15, 8),   -- Siracha
  (15, 22),  -- Vorlaukur
  (15, 23);  -- Pikklaður chili

-- Osta franskar (item 16): cheddar ostasósa, siracha, vorlaukur, pikklaður chili
DELETE FROM item_ingredients WHERE item_id = 16;
INSERT INTO item_ingredients (item_id, ingredient_id) VALUES
  (16, 21),  -- Cheddar ostasósa
  (16, 8),   -- Siracha
  (16, 22),  -- Vorlaukur
  (16, 23);  -- Pikklaður chili
