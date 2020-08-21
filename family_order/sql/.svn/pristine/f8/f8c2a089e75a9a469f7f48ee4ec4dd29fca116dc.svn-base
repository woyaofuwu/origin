SELECT TO_CHAR(MAX(start_date),'yyyy-mm-dd hh24:mi:ss') start_date
  FROM tf_f_user_svcstate
 WHERE user_id = :USER_ID
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND main_tag = '1'
   AND state_code NOT IN ('0','6','8','9','E','F','N')