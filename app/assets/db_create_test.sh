PGPASSWORD=123 psql -h home.xsourse.cc -p 8083 -U postgres -d postgres -f ./db_init.sql
PGPASSWORD=123 psql -h home.xsourse.cc -p 8083 -U postgres -d postgres -f ./db_populate.sql

