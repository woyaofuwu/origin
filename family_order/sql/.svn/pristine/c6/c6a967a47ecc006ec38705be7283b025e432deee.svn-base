SELECT DISTINCT A.REQ_ID,
                A.SERIAL_NUMBER,
                A.CITY_CODE,
                B.TITLE_NAME HCAMPN_NAME,
                B.SALE_ACT_SCRIPT HTEMPLET_CONTENT,
                B.SMS_SCRIPT SMS_CONTENT,
                B.IS_ONE_KEY MOD_NAME,
                B.PRIORITY_LEVEL,
                B.PRO_TYPE_CODE CAMPN_ID,
                B.STEP_ID OBJECT_ID,
                B.SALE_ACT_ID,
                B.PRO_TYPE,
                DECODE(B.PROCESS_TAG,
                       '0',
                       '接受',
                       '1',
                       '拒绝',
                       '2',
                       '犹豫',
                       '无操作') HPROCESS_TAG,
                B.RSRV_NUM1,
                B.RSRV_NUM2,
                B.RSRV_STR1    BUSINESS_CLASS
  FROM TL_OH_REALTIMEMARKETING      A,
       TL_OH_REALTIMEMARKETINGTRADE B
 WHERE A.REQ_ID = B.REQ_ID
   AND A.USER_ID = :USER_ID
   AND A.SERIAL_NUMBER = :SERIAL_NUMBER
   AND A.TRADE_STAFF_ID = :TRADE_STAFF_ID