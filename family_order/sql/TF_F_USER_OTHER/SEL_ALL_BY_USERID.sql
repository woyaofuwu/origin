SELECT partition_id,to_char(user_id) user_id,rsrv_value_code,inst_id,
		rsrv_value,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,
		rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,rsrv_str11,
		to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,
		to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
 FROM  tf_f_user_other
 WHERE partition_id=TO_NUMBER(:PARTITION_ID)
   AND user_id=TO_NUMBER(:USER_ID)
   AND sysdate BETWEEN start_date AND end_date