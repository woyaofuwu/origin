delete from TF_F_LIMIT_BLACKWHITE where serial_number in (select serial_number from 
tf_f_user WHERE user_id=:USER_ID)