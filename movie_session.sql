-- A new table movie_session will show the seat status for each movie session.
-- cinema_id references id in cinema
-- movie_id references id in movie

BEGIN;

DROP TABLE IF EXISTS public.movie_session CASCADE;

CREATE TABLE IF NOT EXISTS public.movie_session
(
    id SERIAL PRIMARY KEY, 
	cinema_id INTEGER NOT NULL,
	movie_id INTEGER NOT NULL,
	showing_date DATE NOT NULL,
	booked_seats INTEGER NOT NULL,
	seats_total INTEGER NOT NULL,
	is_full BOOLEAN NOT NULL
);

ALTER TABLE IF EXISTS public.movie_session
    ADD FOREIGN KEY (cinema_id)
    REFERENCES public.cinema (id)
    NOT VALID;
	
ALTER TABLE IF EXISTS public.movie_session
    ADD FOREIGN KEY (movie_id)
    REFERENCES public.movie (id)
    NOT VALID;

END;

INSERT INTO movie_session (cinema_id, movie_id, showing_date, booked_seats, seats_total, is_full) 
VALUES ((SELECT id FROM cinema WHERE cinema_name='hoits'), (SELECT id FROM movie WHERE name='your name'), '2021-10-20', 23, 40, FALSE),
       ((SELECT id FROM cinema WHERE cinema_name='evants'), (SELECT id FROM movie WHERE name='your name'), '2021-10-30', 10, 50, FALSE),
	   ((SELECT id FROM cinema WHERE cinema_name='hoits'), (SELECT id FROM movie WHERE name='yuri on ice'), '2021-11-01', 5, 45, FALSE),
	   ((SELECT id FROM cinema WHERE cinema_name='odion'), (SELECT id FROM movie WHERE name='avatar'), '2021-10-21', 45, 45, TRUE),
	   ((SELECT id FROM cinema WHERE cinema_name='hoits'), (SELECT id FROM movie WHERE name='avatar'), '2021-11-01', 0, 40, FALSE);