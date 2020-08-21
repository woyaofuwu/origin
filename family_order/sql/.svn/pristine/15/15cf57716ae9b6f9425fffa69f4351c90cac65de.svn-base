select SERIAL_NUMBER,
       to_char(INSERT_TIME, 'yyyy-mm-dd hh24:mi:ss') INSERT_TIME,
       to_char(START_TIME, 'yyyy-mm-dd hh24:mi:ss') START_TIME,
       to_char(END_TIME, 'yyyy-mm-dd hh24:mi:ss') END_TIME
  from TF_F_IMPORT_CUSTOMER
 where SERIAL_NUMBER = :SERIAL_NUMBER
   and END_TIME > sysdate
 order by INSERT_TIME desc