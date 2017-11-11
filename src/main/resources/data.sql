INSERT INTO user (id, username, email, password, has_extended_privileges)  VALUES (1, 'student', 'example@example.com', '$2a$10$5fh0xrsnizwp5WKqSO1kYeMPIeulnjUV.mBsgOGg7hrGoT9ZphcMW', FALSE );
INSERT INTO user (id, username, email, password, has_extended_privileges)  VALUES (2, 'instructor', 'example@example.com', '$2a$10$5fh0xrsnizwp5WKqSO1kYeMPIeulnjUV.mBsgOGg7hrGoT9ZphcMW', TRUE);
INSERT INTO course (id, name, section, rows, cols, instructor_id)  VALUES (1, 'name', 'section', 5, 5, 2);
