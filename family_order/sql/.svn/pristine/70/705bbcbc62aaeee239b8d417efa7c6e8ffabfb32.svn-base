SELECT partition_id,to_char(user_id) user_id,eparchy_code,acyc_id,to_char(score_value) score_value,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM tf_a_score_month
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id=mod(TO_NUMBER(:USER_ID),10000)
   AND acyc_id=:ACYC_ID