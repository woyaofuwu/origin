UPDATE Tf_f_User_Widenet
   SET CONSTRUCTION_ADDR = :DATA11
 where partition_id = mod(to_number(:USER_ID), 10000)
   and user_id = to_number(:USER_ID)
   and sysdate between start_date and end_date