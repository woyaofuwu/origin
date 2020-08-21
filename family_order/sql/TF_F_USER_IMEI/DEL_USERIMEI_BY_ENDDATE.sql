DELETE FROM tf_f_user_imei
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND imei=:IMEI
   AND end_date>sysdate