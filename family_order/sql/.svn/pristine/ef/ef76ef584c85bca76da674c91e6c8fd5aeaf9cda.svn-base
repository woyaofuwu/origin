update tf_f_user_mbmp_plus
   set serial_number = :SERIAL_NUMBER
 where user_id = :USER_ID
   and partition_id = mod(:USER_ID, 10000)