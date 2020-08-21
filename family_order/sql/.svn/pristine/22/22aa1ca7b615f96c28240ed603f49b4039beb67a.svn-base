SELECT PARTITION_ID,to_char(USER_ID) USER_ID,SERVICE_ID,MAIN_TAG,STATE_CODE,to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE,to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME
  FROM tf_f_user_svcstate
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND main_tag = 1
   AND TO_DATE(:OPER_DATE, 'YYYY-MM-DD HH24:MI:SS') BETWEEN start_date AND end_date