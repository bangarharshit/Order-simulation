CREATE TABLE IF NOT EXISTS tickets
(
    ticket_id INT NOT NULL,
    status varchar(250) not null,
    PRIMARY KEY(ticket_id)
    );