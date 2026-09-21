INSERT INTO classification(code, description) VALUES 
	('G', 'General'), 
	('PG', 'Parental Guidance'),
	('M', 'Mature'),
	('MA15+', 'Mature Accompanies'),
	('R18+', 'Restricted');

INSERT INTO cinema(cinema_name) VALUES
	('hoits'),
	('evants'),
	('odion'),
	('netfliz');

INSERT INTO movie(name, classification, synopsis, director, actor_cast) VALUES
	('your name', 'G', 'body swapping', 'Makoto Shinkai', 'Ryunosuke Kamiki'),
	('yuri on ice', 'PG', 'figure skating', 'Sayo Yamamoto', 'Suwabe, Junichi'),
	('avatar', 'M', 'scifi mining colony', 'James Cameron', 'Sam Worthington');

INSERT INTO movie_showing(movie, cinema, available_dates) VALUES
	('your name', 'hoits', '[2021-01-01, 2021-12-31]'),
	('your name', 'evants', '[2021-01-01, 2021-12-31]'),
	('yuri on ice', 'hoits', '[2021-01-01, 2021-12-01]'),
	('avatar', 'odion', '[2021-01-01, 2021-12-31]'),
	('avatar', 'hoits', '[2021-02-01, 2021-12-31]');

INSERT INTO movie_session(movie_showing, showing_date, seats_total, price, screen_size) VALUES
	(1, '2021-11-01', 100, 10, 'Bronze'),
	(1, '2021-11-02', 50, 20, 'Silver'),
	(1, '2021-11-03', 20, 50, 'Gold'),
	(2, '2021-11-04', 100, 10, 'Bronze'),
	(2, '2021-11-03', 50, 20, 'Silver'),
	(3, '2021-11-05', 20, 50, 'Gold'),
    (4, '2021-11-16', 20, 50, 'Gold'),
    (5, '2021-11-11', 20, 50, 'Gold');
INSERT INTO account(username, password, balance, saved_card_number, saved_card_name) VALUES
	('alex', '123', 100.0, NULL, NULL),
	('adam', '123', 100.0, '60146', 'Kasey'),
	('shimiao', '123', 100.0, NULL, NULL),
	('hans', '123', 10.5, NULL, NULL),
	('sung bin', '123', 100.0, NULL, NULL);


INSERT INTO booking(movie_session, seat_location, account) VALUES
	(1, 'front', 'alex'),
	(2, 'middle', 'alex'),
	(3, 'rear', 'alex'),
	(4, 'front', 'adam'),
	(4, 'middle', 'adam'),
	(6, 'rear', 'shimiao'),
	(5, 'front', 'hans'),
	(7, 'middle', 'hans'),
	(8, 'rear', 'hans'),
	(7, 'front', 'shimiao'),
	(8, 'middle', 'shimiao');

INSERT INTO canceled_booking(date_time, account, reason) VALUES
    ('2021-07-15 16:58:39', 'alex', 'card payment failed'),
    ('2021-10-05 05:15:25', 'hans', 'timeout'),
	('2021-11-01 15:21:09', 'hans', 'user cancelled');



INSERT INTO staffaccount(staffname, password) VALUES
	('alex', '123'),
	('adam', '123'),
	('shimiao', '123'),
	('hans', '123'),
	('sung bin', '123');

INSERT INTO manageraccount(managername, password) VALUES
	('manager', '123');

INSERT INTO giftcards (serialNo, redeemable, balance)
VALUES ('1234123412341234', 0, 10.0),
       ('1234123412341235', 0, 50.0),
	   ('1234123412341236', 1, 50.0),
	   ('1234123412341237', 1, 10.0),
	   ('1234123412341238', 1, 100.0),
	   ('1234123412341239', 1, 100.0),
	   ('1234123412341244', 4, 20.0);
