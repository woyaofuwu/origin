SELECT to_char(user_id) user_id,partition_id,brand_code,bcyc_id,score_type_code,to_char(score) score,to_char(score_value) score_value,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5 
  FROM tf_f_user_score_new
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)