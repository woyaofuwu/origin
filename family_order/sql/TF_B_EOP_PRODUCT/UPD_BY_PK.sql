UPDATE TF_B_EOP_PRODUCT T
   SET T.TRADE_ID    = :TRADE_ID,
       T.PRODUCT_TYPE_CODE = :PRODUCT_TYPE_CODE,
       T.PRODUCT_ID = :PRODUCT_ID,
       T.PRODUCT_NAME = :PRODUCT_NAME,
       T.USER_ID = :USER_ID,
       T.PLAN_OPEN_DATE = TO_DATE(:PLAN_OPEN_DATE,
                               'YYYY-MM-DD HH24:MI:SS'),
       T.REAL_OPEN_DATE = TO_DATE(:REAL_OPEN_DATE,
                               'YYYY-MM-DD HH24:MI:SS'),                       
       T.UPDATE_TIME = TO_DATE(:UPDATE_TIME,
                               'YYYY-MM-DD HH24:MI:SS'),
       T.VALID_TAG = :VALID_TAG,
       T.EXEC_MONTH = :EXEC_MONTH,
       T.REMARK = :REMARK,
       T.RSRV_STR1 = :RSRV_STR1,
       T.RSRV_STR2 = :RSRV_STR2,
       T.RSRV_STR3 = :RSRV_STR3,
       T.RSRV_STR4 = :RSRV_STR4,
       T.RSRV_STR5 = :RSRV_STR5
 WHERE T.IBSYSID = :IBSYSID
   AND T.RECORD_NUM = :RECORD_NUM
