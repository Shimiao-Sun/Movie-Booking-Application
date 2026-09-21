# Movie Booking Application

To make changes to the db, first navigate into `./app/assets/`. Then you should update `./db_init.sql` and `./db_populate.sql`. 

Finally to run those sql scripts, run `./db_create_dev.sh`. This will wipe the db and reset it using those two scripts. The connection is currently configured to be 
- home.xsourse.cc:5432 
- username: postgres 
- password: 123 
- database: postgres

For testing, we use a separate db
- home.xsourse.cc:8083
- username: postgres 
- password: 123 
- database: postgres

each time you run a test the db is automatically reset (so you don't need to do anything special here).
