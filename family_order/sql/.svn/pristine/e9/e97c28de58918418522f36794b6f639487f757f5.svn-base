--IS_CACHE=Y
SELECT PACKAGE_ID,a.DISCNT_CODE,PACKAGE_NAME,PACKAGE_TYPE,a.DISCNT_NAME,a.EPARCHY_CODE,to_char(a.START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(a.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE,a.LIMIT_TYPE,a.MONTHS,a.REMARK,a.RSRV_TAG1,NVL(RSRV_TAG2,'0') RSRV_TAG2,a.RSRV_TAG3,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,to_char(RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,to_char(RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,to_char(RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,a.RSRV_STR1,a.RSRV_STR2,a.RSRV_STR3,to_char(a.UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,a.UPDATE_STAFF_ID,a.UPDATE_DEPART_ID
  FROM TD_B_DISCNT_PACKAGE a
 WHERE a.package_id = :PACKAGE_ID
   AND a.package_type = :PACKAGE_TYPE
   AND (a.eparchy_code = :EPARCHY_CODE OR a.eparchy_code = 'ZZZZ')
   AND SYSDATE BETWEEN a.start_date AND a.end_date