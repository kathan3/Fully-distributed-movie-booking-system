
CREATE TABLE Theatre (
    id INTEGER PRIMARY KEY,
    name VARCHAR(255),
    location VARCHAR(255)
);
CREATE TABLE Show (
    id INTEGER PRIMARY KEY,
    theatre_id INTEGER,
    title VARCHAR(255),
    price INTEGER,
    seats_available INTEGER
);
CREATE TABLE Booking (
    id INTEGER PRIMARY KEY,
    show_id INTEGER,
    user_id INTEGER,
    seats_booked INTEGER
);

INSERT INTO Theatre (id, name, location)
VALUES
    (1, 'Helen Hayes Theater', '240 W 44th St.'),
    (2, 'Cherry Lane Theatre', '38 Commerce Street'),
    (3, 'New World Stages', '340 West 50th Street'),
    (4, 'The Zipper Theater', '100 E 17th St'),
    (5, 'Queens Theatre', 'Meadows Corona Park'),
    (6, 'The Public Theater', '425 Lafayette St'),
    (7, 'Manhattan Ensemble Theatre', '55 Mercer St.'),
    (8, 'Metropolitan Playhouse', '220 E 4th St.'),
    (9, 'Acorn Theater', '410 West 42nd Street'),
    (10, 'Apollo Theater', '253 West 125th Street');

INSERT INTO Show (id, theatre_id, title, price, seats_available)
VALUES
    (1, 1, 'Youth in Revolt', 50, 40),
    (2, 1, 'Leap Year', 55, 30),
    (3, 1, 'Remember Me', 60, 55),
    (4, 2, 'Fireproof', 65, 65),
    (5, 2, 'Beginners', 55, 50),
    (6, 3, 'Music and Lyrics', 75, 40),
    (7, 3, 'The Back-up Plan', 65, 60),
    (8, 4, 'WALL-E', 45, 55),
    (9, 4, 'Water For Elephants', 50, 45),
    (10, 5, 'What Happens in Vegas', 65, 65),
    (11, 6, 'Tangled', 55, 40),
    (12, 6, 'The Curious Case of Benjamin Button', 65, 50),
    (13, 7, 'Rachel Getting Married', 40, 60),
    (14, 7, 'New Year''s Eve', 35, 45),
    (15, 7, 'The Proposal', 45, 55),
    (16, 8, 'The Time Traveler''s Wife', 75, 65),
    (17, 8, 'The Invention of Lying', 50, 40),
    (18, 9, 'The Heartbreak Kid', 60, 50),
    (19, 10, 'The Duchess', 70, 60),
    (20, 10, 'Mamma Mia!', 40, 45);
