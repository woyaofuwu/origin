select C.PARTITION_ID,
       C.USER_ID,
       C.USER_ID_A,
       C.SERVICE_ID,
       C.MAIN_TAG,
       C.INST_ID,
       C.CAMPN_ID,
       C.START_DATE,
       C.END_DATE,
       C.UPDATE_TIME,
       C.UPDATE_STAFF_ID,
       C.UPDATE_DEPART_ID,
       C.REMARK,
       C.RSRV_NUM1,
       C.RSRV_NUM2,
       C.RSRV_NUM3,
       C.RSRV_NUM4,
       C.RSRV_NUM5,
       C.RSRV_STR1,
       C.RSRV_STR2,
       C.RSRV_STR3,
       C.RSRV_STR4,
       C.RSRV_STR5,
       C.RSRV_DATE1,
       C.RSRV_DATE2,
       C.RSRV_DATE3,
       C.RSRV_TAG1,
       C.RSRV_TAG2,
       C.RSRV_TAG3,
       C.RSRV_STR6,
       C.RSRV_STR7,
       C.RSRV_STR8,
       C.RSRV_STR9,
       C.RSRV_STR10
  from TF_F_USER_SVC C
 where C.USER_ID = TO_NUMBER(:USER_ID)
 AND C.PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000)
 AND C.SERVICE_ID =TO_NUMBER(:SERVICE_ID)
 AND C.END_DATE>SYSDATE