-- Ensure items have tags so the app can filter by Vegetarian / Meat.
-- Run in Supabase Dashboard → SQL Editor → New query, then Run.
--
-- Tags: comma-separated. Use 'vegan' for vegetarian, 'meat' for meat, 'meat,vegan' for both options.

-- 1. Add column if it doesn't exist (e.g. old DB)
ALTER TABLE items
  ADD COLUMN IF NOT EXISTS tags TEXT NOT NULL DEFAULT '';

-- 2. Set tags for Haga-vagninn items (by id). Adjust if your item ids differ.
--    Vegetarian filter = items with 'vegan' in tags
--    Meat filter = items with 'meat' in tags

UPDATE items SET tags = 'meat,vegan' WHERE id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
UPDATE items SET tags = 'vegan' WHERE id IN (12, 13, 14, 15, 16, 18, 19);
UPDATE items SET tags = '' WHERE id = 17;

-- If your items have different ids, set by name instead (uncomment and adjust):
-- UPDATE items SET tags = 'meat,vegan' WHERE name ILIKE '%börger%' OR name ILIKE '%burger%';
-- UPDATE items SET tags = 'vegan' WHERE name ILIKE '%vegan%' OR name ILIKE '%franskar%' OR name ILIKE '%gos%' OR name ILIKE '%safi%' OR name ILIKE '%djús%';
