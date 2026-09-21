--Table that consists of giftcards

BEGIN;

DROP TABLE IF EXISTS public.giftcards CASCADE;

CREATE TABLE IF NOT EXISTS public.giftcards
(
    serialNo CHAR(16) PRIMARY KEY, 
	redeemable INTEGER NOT NULL,
	balance double NOT NULL
);

END;

INSERT INTO giftcards (serialNo, redeemable) 
VALUES (('1234123412341234', 0, 30.00),
       (('1234123412341235', 0. 30.00),
	   (('1234123412341236', 1, 100.00),
	   (('1234123412341237', 1, 10.00),
	   (('1234123412341238', 1, 140.00);
	   