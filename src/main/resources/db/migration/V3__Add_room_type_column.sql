-- Add room_type column to rooms table
ALTER TABLE rooms ADD COLUMN room_type VARCHAR(20) DEFAULT 'PHONG_TRO';

-- Update existing records to have default room type
UPDATE rooms SET room_type = 'PHONG_TRO' WHERE room_type IS NULL;

-- Add constraint to ensure room_type is not null
ALTER TABLE rooms MODIFY COLUMN room_type VARCHAR(20) NOT NULL DEFAULT 'PHONG_TRO';
