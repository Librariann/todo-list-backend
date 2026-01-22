-- Add point column to user_points table
ALTER TABLE user_points ADD COLUMN point INTEGER NOT NULL DEFAULT 0;