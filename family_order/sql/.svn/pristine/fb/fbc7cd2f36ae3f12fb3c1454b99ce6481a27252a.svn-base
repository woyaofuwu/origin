select INST_ID user_id,user_type_code,biz_in_code,ec_serial_number,serv_code,biz_code,biz_name,start_date,end_date
from TF_F_USER_BLACKWHITE  
WHERE EC_USER_ID=:EC_USER_ID
and service_ID=:SERVICE_ID 
AND SERIAL_NUMBER=:SERIAL_NUMBER
AND sysdate BETWEEN start_date AND end_date