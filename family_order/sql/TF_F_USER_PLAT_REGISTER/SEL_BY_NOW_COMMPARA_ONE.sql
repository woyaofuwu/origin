SELECT PARTITION_ID,to_char(USER_ID) USER_ID,SERIAL_NUMBER,BIZ_TYPE_CODE,ORG_DOMAIN,OPR_SOURCE,PASSWD,BIZ_STATE_CODE,OPEN_TAG,SVC_LEVEL,to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,to_char(RSRV_NUM4) RSRV_NUM4,to_char(RSRV_NUM5) RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,to_char(RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,to_char(RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,to_char(RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,REMARK,UPDATE_STAFF_ID,UPDATE_DEPART_ID,to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME
  FROM TF_F_USER_PLAT_REGISTER a
 WHERE a.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND a.user_id=TO_NUMBER(:USER_ID)
   AND sysdate BETWEEN a.start_date+0 AND a.end_date+0
   AND EXISTS(SELECT 1 FROM td_s_commpara 
              WHERE param_attr = 934
              AND param_code = a.biz_type_code
              AND para_code1 = :SERVICE_TYPE
              AND sysdate BETWEEN start_date AND end_date)