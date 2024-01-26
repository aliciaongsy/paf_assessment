-- Write your Task 1 answers in this file
drop database if exists bedandbreakfast;

create database bedandbreakfast;

use bedandbreakfast;

create table users (
    email varchar(128) not null,
    name varchar(128),

    primary key(email)
);

create table bookings (
    booking_id char(8) not null,
    listing_id varchar(20),
    duration int,
    email varchar(128),

    primary key(booking_id),
    FOREIGN KEY (email) REFERENCES users(email)
);

create table reviews (
    id int not null,
    date timestamp default current_timestamp,
    listing_id varchar(20),
    reviewer_name varchar(64),
    comments text,

    primary key(id)
);

grant all privileges on bedandbreakfast.* to fred@'%';
flush privileges;

SHOW GLOBAL VARIABLES LIKE 'local_infile';
SET GLOBAL local_infile = 'ON';

load data local infile '/Users/aliciaong/VTTP/assessment/paf_assessment_template/data/users.csv' into table users fields terminated by ','
    LINES TERMINATED BY '\n'
    IGNORE 1 LINES;

-- insert into users(email, name) 
-- values (fred@gmail.com, Fred Flintstone),
-- (barney@gmail.com, Barney Rubble),
-- (fry@planetexpress.com, Philip J Fry),
-- (hlmer@gmail.com, Homer Simpson);
