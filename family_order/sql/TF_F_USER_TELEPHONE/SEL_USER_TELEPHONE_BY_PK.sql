select partition_id,INST_ID,user_id,stand_address,detail_address,sign_path,port_type,secret,
stand_address_code,start_date,end_date 
from Tf_f_User_Telephone 
where user_id = :USER_ID 
and partition_id = mod(to_number(:USER_ID),10000)
and start_date+0< sysdate