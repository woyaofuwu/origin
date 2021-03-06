SELECT PARTITION_ID,
   USER_ID,
   OPR_NUMB,
   SERIAL_NUMBER,
   CHANNEL_ID,
   PASSWORD,
   PWD_TYPE,
   PWD_START_TIME,
   PWD_END_TIME,
   PWD_ACTIVE_TIME,
   PWD_FLAG,
   EPARCHY_CODE,
   UPDATE_TIME,
   UPDATE_STAFF_ID,
   UPDATE_DEPART_ID,
   REMARK,
   RSRV_STR1,
   RSRV_STR2,
   RSRV_STR3,
   RSRV_STR4,
   RSRV_STR5,
   RSRV_TAG1,
   RSRV_TAG2,
   RSRV_TAG3
   from TF_B_PWD_LOG WHERE 1=1 AND PASSWORD= :PASSWORD AND SERIAL_NUMBER = :SERIAL_NUMBER AND USER_ID= :USER_ID AND PARTITION_ID= MOD(:USER_ID, 10000) AND sysdate between PWD_START_TIME and PWD_END_TIME ORDER BY PWD_END_TIME DESC