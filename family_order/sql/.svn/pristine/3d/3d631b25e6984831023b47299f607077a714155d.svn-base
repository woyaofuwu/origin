SELECT PARTITION_ID,to_char(USER_ID) USER_ID,to_char(SERIAL_NUMBER) SERIAL_NUMBER,BIZ_CODE,BIZ_NAME,to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE,to_char(EC_USER_ID) EC_USER_ID,to_char(EC_SERIAL_NUMBER) EC_SERIAL_NUMBER,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,to_char(RSRV_NUM4) RSRV_NUM4,to_char(RSRV_NUM5) RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,to_char(RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,to_char(RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,to_char(RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,REMARK,UPDATE_STAFF_ID,UPDATE_DEPART_ID,to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME
  FROM TF_F_USER_GRPMBMP_SUB a 
 WHERE a.partition_id =MOD(TO_NUMBER(:USER_ID),10000)
   AND a.user_id =TO_NUMBER(:USER_ID)
  AND SYSDATE BETWEEN a.START_DATE AND a.END_DATE
  and EXISTS (
      SELECT 1
          FROM td_s_commpara
         WHERE subsys_code='CSM'      
           AND param_attr = '1340' 
           AND para_code1 = a.rsrv_str1
           AND sysdate BETWEEN start_date AND end_date)