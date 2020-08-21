update tf_f_user_mbmp
   set serial_number = :SERIAL_NUMBER ,update_time=sysdate 
 where user_id = :USER_ID
   and partition_id = mod(:USER_ID, 10000)
   and end_date>sysdate