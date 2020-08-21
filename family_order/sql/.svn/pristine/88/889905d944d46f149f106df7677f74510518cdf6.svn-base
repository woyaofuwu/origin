SELECT PARTITION_ID,
       to_char(USER_ID) USER_ID,
       INST_ID,
       SERVICE_ID,
       SERVICE_ID,
       MAIN_TAG,
       to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       UPDATE_TIME,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       REMARK,
       RSRV_NUM1,
       RSRV_NUM2,
       RSRV_NUM3,
       RSRV_NUM4,
       RSRV_NUM5,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       RSRV_STR5,
       RSRV_DATE1,
       RSRV_DATE2,
       RSRV_DATE3,
       RSRV_TAG1,
       RSRV_TAG2,
       RSRV_TAG3
  FROM tf_f_user_svc t 
 WHERE t.user_id = TO_NUMBER(:USER_ID)
   AND t.partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND sysdate between t.start_date and t.end_date
 ORDER BY t.service_id
