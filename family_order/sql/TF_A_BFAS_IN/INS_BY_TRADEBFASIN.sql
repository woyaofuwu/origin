INSERT INTO TF_A_BFAS_IN
  (BFAS_ID, SYS_CODE, LOG_ID, SUB_LOG_ID, PARTITION_ID, EPARCHY_CODE,
   CITY_CODE, PROFIT_CEN_ID, DEPART_ID, AGENT_CODE, OPER_STAFF_ID,
   OPER_TYPE_CODE, SALE_TYPE_CODE, PAY_MONEY_CODE, CAMPN_ID, FEE_TYPE_CODE,
   FEE_ITEM_TYPE_CODE, PAY_MODE_CODE, ACCT_ID, COLL_AGEN_CODE, LOGOUT_TAG,
   IN_MODE_CODE, NET_TYPE_CODE, BRAND_CODE, PRODUCT_ID, USER_TYPE_CODE,
   CHECK_NUMBER, RES_TYPE_CODE, RES_KIND_CODE, CAPACITY_TYPE_CODE,
   DEVICE_TYPE_CODE, DEVICE_MODEL_CODE, DEVICE_COLOR_CODE, ORDER_ID,
   DEVICE_PRODUCT, SUPPLY_TYPE, PROCUREMENT_TYPE, AGENCY_ID, INCOME_CODE,
   RECE_FEE, FEE, PRESENT_FEE, FORM_FEE, SCORE, ACC_DATE, OPER_DATE,
   CANCEL_SUB_LOG_ID, CANCEL_DATE, CANCEL_TAG, DEPOSIT_BEGIN_DATE,
   DEPOSIT_END_DATE, PROC_TAG, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3, RSRV_STR1,
   RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, RSRV_STR7,
   RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, RSRV_NUM4, RSRV_NUM5, RSRV_DATE1,
   RSRV_DATE2, RSRV_DATE3, NO_TAX_FEE, TAX_FEE, TAX_RATE)
  SELECT BFAS_ID, SYS_CODE, LOG_ID, SUB_LOG_ID, PARTITION_ID, EPARCHY_CODE,
         CITY_CODE, PROFIT_CEN_ID, DEPART_ID, AGENT_CODE, OPER_STAFF_ID,
         OPER_TYPE_CODE, SALE_TYPE_CODE, PAY_MONEY_CODE, CAMPN_ID,
         FEE_TYPE_CODE, FEE_ITEM_TYPE_CODE, PAY_MODE_CODE, ACCT_ID,
         COLL_AGEN_CODE, LOGOUT_TAG, IN_MODE_CODE, NET_TYPE_CODE, BRAND_CODE,
         PRODUCT_ID, USER_TYPE_CODE, CHECK_NUMBER, RES_TYPE_CODE,
         RES_KIND_CODE, CAPACITY_TYPE_CODE, DEVICE_TYPE_CODE,
         DEVICE_MODEL_CODE, DEVICE_COLOR_CODE, ORDER_ID, DEVICE_PRODUCT,
         SUPPLY_TYPE, PROCUREMENT_TYPE, AGENCY_ID, INCOME_CODE, RECE_FEE,
         FEE, PRESENT_FEE, FORM_FEE, SCORE, SYSDATE, OPER_DATE,
         CANCEL_SUB_LOG_ID, CANCEL_DATE, CANCEL_TAG, DEPOSIT_BEGIN_DATE,
         DEPOSIT_END_DATE, PROC_TAG, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3,
         RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6,
         RSRV_STR7, RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, RSRV_NUM4, RSRV_NUM5,
         RSRV_DATE1, RSRV_DATE2, RSRV_DATE3, NO_TAX_FEE, TAX_FEE, RATE
    FROM TF_B_TRADE_BFAS_IN
   WHERE SUB_LOG_ID = :SUB_LOG_ID
     AND PARTITION_ID = TO_NUMBER(TO_CHAR(SYSDATE,'MM'))
     AND CANCEL_TAG = :CANCEL_TAG
     AND NOT EXISTS
   (SELECT 1
            FROM TF_A_BFAS_IN
           WHERE SUB_LOG_ID = :SUB_LOG_ID
           	 AND (CANCEL_TAG = :CANCEL_TAG OR CANCEL_TAG IS NULL)
			 AND PARTITION_ID = TO_NUMBER(TO_CHAR(SYSDATE,'MM'))
             )