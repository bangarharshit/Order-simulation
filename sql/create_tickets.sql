DELETE * FROM tickets;

INSERT INTO tickets
select id, 'not_taken'
FROM GENERATE_SERIES(1, 120) as id;