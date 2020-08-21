DELETE FROM tf_f_user_bind
 WHERE user_id = to_number(:USER_ID)
   AND bind_serial_number = :BIND_SERIAL_NUMBER