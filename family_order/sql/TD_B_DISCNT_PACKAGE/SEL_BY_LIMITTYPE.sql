SELECT PACKAGE_ID,DISCNT_CODE,PACKAGE_NAME,PACKAGE_TYPE,DISCNT_NAME,EPARCHY_CODE,to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE,LIMIT_TYPE,MONTHS,REMARK,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,to_char(RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,to_char(RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,to_char(RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,RSRV_STR1,RSRV_STR2,RSRV_STR3,to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID
  FROM TD_B_DISCNT_PACKAGE a
 WHERE (a.discnt_code = :DISCNT_CODE OR :DISCNT_CODE = -1)
   AND (a.limit_type = :LIMIT_TYPE OR :LIMIT_TYPE IS NULL)
   AND (a.package_type = :PACKAGE_TYPE OR :PACKAGE_TYPE  IS NULL)
   AND (a.eparchy_code = :EPARCHY_CODE OR a.eparchy_code = 'ZZZZ')
   AND SYSDATE BETWEEN a.start_date AND a.end_date
   AND EXISTS(SELECT 1 FROM tf_f_user_discnt b
                      WHERE a.discnt_code+0 = b.discnt_code+0
                        AND b.user_id = TO_NUMBER(:USER_ID)
                        AND b.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
                        AND b.end_date>SYSDATE)