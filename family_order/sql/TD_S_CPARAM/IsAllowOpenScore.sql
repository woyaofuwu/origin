SELECT COUNT(1) recordcount
 FROM tf_f_user_other
 WHERE partition_id = mod(to_number(:USER_ID),10000)
   AND user_id = to_number(:USER_ID)
   AND rsrv_value_code=to_char(:RSRV_VALUE_CODE)
   AND sysdate between start_date and  end_date