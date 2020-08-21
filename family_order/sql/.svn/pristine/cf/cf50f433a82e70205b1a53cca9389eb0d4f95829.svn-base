select user_id,cust_id,serial_number,eparchy_code,
 b.user_passwd user_passwd,
f_csb_encrypt(b.user_passwd,user_id) deposit_name  
from   tf_f_user, 
(SELECT SUBSTR(TO_CHAR(abs(DBMS_RANDOM.random())) || '000000',1,6) user_passwd FROM dual WHERE ROWNUM<2
) b
where serial_number=:SERIAL_NUMBER and  remove_tag=:REMOVE_TAG