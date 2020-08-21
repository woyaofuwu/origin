UPDATE tf_f_user
   SET user_state_codeset = NVL(:USER_STATE_CODESET,'0'),
       rsrv_str1 = :RSRV_STR1,
       last_stop_time = NVL(TO_DATE(:LAST_STOP_TIME,'yyyy-mm-dd hh24:mi:ss'),last_stop_time)
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)