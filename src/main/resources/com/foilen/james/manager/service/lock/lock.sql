CREATE TABLE IF NOT EXISTS `FOILEN_LOCK`  (
  name varchar(50) primary key, 
  requestorId varchar(100),
  until datetime);

