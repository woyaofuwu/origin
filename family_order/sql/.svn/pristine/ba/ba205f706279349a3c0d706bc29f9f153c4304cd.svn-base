SELECT TO_CHAR(U.USER_ID) USER_ID,
       TO_CHAR(U.CUST_ID) CUST_ID,
       TO_CHAR(U.USECUST_ID) USECUST_ID,
       U.EPARCHY_CODE,
       U.CITY_CODE,
       U.USER_PASSWD,
       U.USER_TYPE_CODE,
       U.SERIAL_NUMBER,
       U.ACCT_TAG,
       U.PREPAY_TAG,
       TO_CHAR(U.IN_DATE, 'yyyy-mm-dd hh24:mi:ss') IN_DATE,
       TO_CHAR(U.OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE,
       U.REMOVE_TAG,
       TO_CHAR(U.DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') DESTROY_TIME,
       TO_CHAR(U.PRE_DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') PRE_DESTROY_TIME,
       TO_CHAR(U.FIRST_CALL_TIME, 'yyyy-mm-dd hh24:mi:ss') FIRST_CALL_TIME,
       TO_CHAR(U.LAST_STOP_TIME, 'yyyy-mm-dd hh24:mi:ss') LAST_STOP_TIME,
       U.OPEN_MODE,
       U. USER_STATE_CODESET,
       U.MPUTE_MONTH_FEE,
       TO_CHAR(U.MPUTE_DATE, 'yyyy-mm-dd hh24:mi:ss') MPUTE_DATE,
       U.RSRV_STR1,
       U.RSRV_STR2,
       U.RSRV_STR3,
       U.RSRV_STR4,
       U.RSRV_STR5,
       U.RSRV_STR6,
       U.RSRV_STR7,
       U.RSRV_STR8,
       U.RSRV_STR9,
       U.RSRV_STR10,
       TO_CHAR(U.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       P.PRODUCT_ID,
       '' MODIFY_TAG
  FROM TF_F_USER U, TF_F_USER_PRODUCT P
 WHERE 1 = 1
   AND U.USER_ID = P.USER_ID
   AND U.PARTITION_ID = P.PARTITION_ID
   AND U.REMOVE_TAG = '0'
   AND U.CUST_ID = TO_NUMBER(:CUST_ID)
 ORDER BY OPEN_DATE DESC
