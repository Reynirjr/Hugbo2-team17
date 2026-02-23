-- Update items to match menu data: correct tags and add meðlæta/drykkir for filters.
-- Run in Supabase Dashboard → SQL Editor.
-- Sections: 1 = Máltíðir, 2 = Borgarar, 3 = Aukavörur (meðlæta), 4 = Drykkir (drykkir).

-- 1. Set exact tags per item (from items table data)
UPDATE items SET name = 'Double Börger', description = 'Börger með tvöföldu kjöti og osti, franskar & gos', price_isk = 3290, available = true, tags = 'meat' WHERE id = 1;
UPDATE items SET name = 'Börger', description = 'Börger, franskar & gos', price_isk = 2690, available = true, tags = 'meat,vegan' WHERE id = 2;
UPDATE items SET name = 'Fjölskyldu tilboð', description = '2x Börger, 2x barna börgerar, stórar franskar', price_isk = 6990, available = true, tags = 'meat,vegan' WHERE id = 3;
UPDATE items SET name = 'Barna börger', description = 'Barna börger, franskar & djús (fyrir 12 ára og yngri)', price_isk = 1590, available = true, tags = 'meat,vegan' WHERE id = 4;
UPDATE items SET name = 'Hagabörger einfaldur', description = 'Ostur, pikklaður laukur, pikklaðar gúrkur, mæjó & sinnep. Kjöt eða veganbuff.', price_isk = 1690, available = true, tags = 'meat,vegan' WHERE id = 5;
UPDATE items SET name = 'Hagabörger tvöfaldur', description = 'Ostur, pikklaður laukur, pikklaðar gúrkur, mæjó & sinnep. Kjöt eða veganbuff.', price_isk = 2490, available = true, tags = 'meat,vegan' WHERE id = 6;
UPDATE items SET name = 'Laukbörger einfaldur', description = 'Ostur, rautt relish, karmellíseraður laukur, mæjó & sinnep. Kjöt eða veganbuff.', price_isk = 1690, available = true, tags = 'meat' WHERE id = 7;
UPDATE items SET name = 'Laukbörger tvöfaldur', description = 'Ostur, rautt relish, karmellíseraður laukur, mæjó & sinnep. Kjöt eða veganbuff.', price_isk = 2490, available = true, tags = 'meat' WHERE id = 8;
UPDATE items SET name = 'Ostabörger einfaldur', description = 'Ostur, laukur, kál, tómatar, mæjó, sinnep & tómatSósa. Kjöt eða veganbuff.', price_isk = 1690, available = true, tags = 'meat,vegan' WHERE id = 9;
UPDATE items SET name = 'Ostabörger tvöfaldur', description = 'Ostur, laukur, kál, tómatar, mæjó, sinnep & tómatSósa. Kjöt eða veganbuff.', price_isk = 2490, available = true, tags = 'meat,vegan' WHERE id = 10;
UPDATE items SET name = 'Barnabörger', description = 'Kjöt eða veganbuff, ostur & tómatSósa', price_isk = 1390, available = true, tags = 'meat,vegan' WHERE id = 11;
UPDATE items SET name = 'Spæsí vegan vængir', description = 'Blómkál, tempura, chili, graslaukur & sriracha', price_isk = 1490, available = true, tags = 'vegan' WHERE id = 12;
UPDATE items SET name = 'Franskar lítill', description = 'Franskar', price_isk = 790, available = true, tags = 'vegan' WHERE id = 13;
UPDATE items SET name = 'Franskar stór', description = 'Stórar franskar', price_isk = 1690, available = true, tags = 'vegan' WHERE id = 14;
UPDATE items SET name = 'Spæsí franskar', description = 'Franskar með kryddi', price_isk = 1490, available = true, tags = 'vegan' WHERE id = 15;
UPDATE items SET name = 'Osta franskar', description = 'Franskar með osti', price_isk = 1490, available = true, tags = 'vegan' WHERE id = 16;
UPDATE items SET name = 'Sósur', description = 'Sósur á la carte', price_isk = 390, available = true, tags = '' WHERE id = 17;
UPDATE items SET name = 'Gos í dós', description = 'Coke, Coke Zero, Appelsín, Toppur', price_isk = 390, available = true, tags = 'vegan' WHERE id = 18;
UPDATE items SET name = 'Safi', description = 'Djús', price_isk = 290, available = true, tags = 'vegan' WHERE id = 19;

-- 2. Add meðlæta tag to Aukavörur (section_id = 3): sides
UPDATE items SET tags = CASE WHEN tags = '' OR tags IS NULL THEN 'meðlæta' ELSE tags || ',meðlæta' END WHERE section_id = 3;

-- 3. Add drykkir tag to Drykkir (section_id = 4): drinks
UPDATE items SET tags = CASE WHEN tags = '' OR tags IS NULL THEN 'drykkir' ELSE tags || ',drykkir' END WHERE section_id = 4;

-- Optional: remove per-item estimated wait (restaurant uses store_settings.queue_minutes instead).
-- Uncomment if you want to drop the column from items:
-- ALTER TABLE items DROP COLUMN IF EXISTS estimated_wait_time_minutes;
