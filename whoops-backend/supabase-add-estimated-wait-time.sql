-- Add estimated wait time per item (matches Item.estimatedWaitTimeMinutes in class diagram).
-- Run in Supabase Dashboard → SQL Editor → New query, then Run.

ALTER TABLE items
  ADD COLUMN IF NOT EXISTS estimated_wait_time_minutes INTEGER;

-- Required: set values so the app can show them (otherwise column is NULL and nothing appears)
UPDATE items SET estimated_wait_time_minutes = 10 WHERE estimated_wait_time_minutes IS NULL;
