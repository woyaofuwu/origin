SELECT user_pass,phone_nbr FROM chnl_access_phone WHERE phone_nbr=(SELECT max(user_id) FROM tf_f_user WHERE serial_number=:SERIAL_NUMBER)
and state=0