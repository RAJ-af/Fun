-- Add participants column to voice_rooms if it doesn't exist
ALTER TABLE voice_rooms ADD COLUMN IF NOT EXISTS participants INTEGER DEFAULT 0;

-- Function to update participant count
CREATE OR REPLACE FUNCTION update_voice_room_participant_count()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        UPDATE voice_rooms
        SET participants = (SELECT count(*) FROM room_participants WHERE room_id = NEW.room_id)
        WHERE id = NEW.room_id;
        RETURN NEW;
    ELSIF (TG_OP = 'DELETE') THEN
        UPDATE voice_rooms
        SET participants = (SELECT count(*) FROM room_participants WHERE room_id = OLD.room_id)
        WHERE id = OLD.room_id;
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Trigger to update participant count automatically
DROP TRIGGER IF EXISTS tr_update_participant_count ON room_participants;
CREATE TRIGGER tr_update_participant_count
AFTER INSERT OR DELETE ON room_participants
FOR EACH ROW
EXECUTE FUNCTION update_voice_room_participant_count();

-- Function to cleanup empty rooms (no host or speaker) older than 5 minutes
CREATE OR REPLACE FUNCTION cleanup_empty_voice_rooms()
RETURNS void AS $$
BEGIN
    DELETE FROM voice_rooms
    WHERE id NOT IN (
        SELECT DISTINCT room_id
        FROM room_participants
        WHERE role IN ('host', 'speaker')
    )
    AND created_at < NOW() - INTERVAL '5 minutes';
END;
$$ LANGUAGE plpgsql;

-- Note: To run this periodically, you would normally enable pg_cron in Supabase:
-- SELECT cron.schedule('*/5 * * * *', 'SELECT cleanup_empty_voice_rooms()');
