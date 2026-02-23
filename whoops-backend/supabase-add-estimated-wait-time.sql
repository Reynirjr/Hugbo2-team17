-- Add estimated wait time per item (matches Item.estimatedWaitTimeMinutes in class diagram).
-- Run in Supabase Dashboard → SQL Editor → New query, then Run.

ALTER TABLE items
  ADD COLUMN IF NOT EXISTS estimated_wait_time_minutes INTEGER;

-- Optional: give existing items a default so the app shows a value (e.g. 10 min)
-- UPDATE items SET estimated_wait_time_minutes = 10 WHERE estimated_wait_time_minutes IS NULL;
