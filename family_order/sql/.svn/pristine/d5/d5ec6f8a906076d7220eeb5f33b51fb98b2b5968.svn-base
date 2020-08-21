select   
   PARTITION_ID, to_char(USER_ID) USER_ID, to_char(USER_ID_A) USER_ID_A, to_char(PLAN_ID) PLAN_ID,PLAN_TYPE_CODE, PLAN_NAME, PLAN_DESC, DEFAULT_TAG, RULE_ID, to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10
 from tf_f_user_payplan
 where
   USER_ID=:USER_ID 
   and  partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   and  USER_ID_A=:USER_ID_A
   and  sysdate between START_DATE and  END_DATE