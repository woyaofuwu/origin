DELETE FROM tf_f_user_imei
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND start_date>=end_date