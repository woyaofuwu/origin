SELECT TO_CHAR(TRADE_ID) TRADE_ID,
       ACCEPT_MONTH,
       TO_CHAR(USER_ID) USER_ID,
       SERVICE_ID,
       SERIAL_NUMBER,
       INFO_CODE,
       INFO_VALUE,
       INFO_NAME,
       TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       REMARK,
       RSRV_NUM1,
       RSRV_NUM2,
       RSRV_STR1,
       RSRV_STR2,
       TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
       TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
       TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3
  FROM TF_B_TRADE_PLATSVC_ATTR
 WHERE trade_id = :TRADE_ID
        AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))