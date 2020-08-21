SELECT to_char(user_id) user_id,serial_number,imei,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_imei
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND start_date=(SELECT MAX(start_date)
		    FROM tf_f_user_imei
		    WHERE user_id=TO_NUMBER(:USER_ID))