update tf_b_pwd_log 
set PWD_FLAG = '1' 
where serial_number = :SERIAL_NUMBER 
AND sysdate between PWD_START_TIME 
and PWD_END_TIME