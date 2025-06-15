INSERT INTO users (id, name, email)
VALUES  (1, 'name1', 'email1'),
        (2, 'name2', 'email2'),
        (3, 'name3', 'email3'),
        (4, 'name4', 'email4'),
        (5, 'name5', 'email5'),
        (6, 'name6', 'email6'),
        (7, 'name7', 'email7'),
        (8, 'name8', 'email8'),
        (9, 'name9', 'email9'),
        (10, 'name10', 'email10');
SELECT SETVAL('users_id_seq', (SELECT MAX(id) FROM users));