PGPASSWORD=123 psql -h home.xsourse.cc -p 5432 -U postgres -d postgres -f ./db_init.sql
PGPASSWORD=123 psql -h home.xsourse.cc -p 5432 -U postgres -d postgres -f ./db_populate.sql

