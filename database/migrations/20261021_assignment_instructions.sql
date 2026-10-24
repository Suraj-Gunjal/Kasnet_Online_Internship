-- Apply to the existing online_internship database; do not run database.sql
-- against an existing installation. This migration is safe to re-run.
DROP PROCEDURE IF EXISTS kasnet_add_assignment_instruction_fields;

DELIMITER //
CREATE PROCEDURE kasnet_add_assignment_instruction_fields()
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'assignments'
          AND column_name = 'instructions_text'
    ) THEN
        ALTER TABLE assignments
            ADD COLUMN instructions_text TEXT NULL AFTER description;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'assignments'
          AND column_name = 'instruction_pdf_path'
    ) THEN
        ALTER TABLE assignments
            ADD COLUMN instruction_pdf_path VARCHAR(255) NULL AFTER instructions_text;
    END IF;

END//
DELIMITER ;

CALL kasnet_add_assignment_instruction_fields();
DROP PROCEDURE kasnet_add_assignment_instruction_fields;
