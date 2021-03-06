SELECT B.PARTITION_ID,
       B.USER_ID,
       B.CUST_ID,
       B.USECUST_ID,
       B.EPARCHY_CODE,
       B.CITY_CODE,
       B.CITY_CODE_A,
       B.USER_PASSWD,
       B.USER_DIFF_CODE,
       B.USER_TYPE_CODE,
       B.USER_TAG_SET,
       B.USER_STATE_CODESET,
       B.NET_TYPE_CODE,
       B.SERIAL_NUMBER,
       B.CONTRACT_ID,
       B.ACCT_TAG,
       B.PREPAY_TAG,
       B.MPUTE_MONTH_FEE,
       to_char(B.MPUTE_DATE, 'yyyy-mm-dd hh24:mi:ss') MPUTE_DATE,
       to_char(B.FIRST_CALL_TIME, 'yyyy-mm-dd hh24:mi:ss') FIRST_CALL_TIME,
       to_char(B.LAST_STOP_TIME, 'yyyy-mm-dd hh24:mi:ss') LAST_STOP_TIME,
       to_char(B.CHANGEUSER_DATE, 'yyyy-mm-dd hh24:mi:ss') CHANGEUSER_DATE,
       B.IN_NET_MODE,
       to_char(B.IN_DATE, 'yyyy-mm-dd hh24:mi:ss') IN_DATE,
       B.IN_STAFF_ID,
       B.IN_DEPART_ID,
       B.OPEN_MODE,
       to_char(B.OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE,
       B.OPEN_STAFF_ID,
       B.OPEN_DEPART_ID,
       B.DEVELOP_STAFF_ID,
       to_char(B.DEVELOP_DATE, 'yyyy-mm-dd hh24:mi:ss') DEVELOP_DATE,
       B.DEVELOP_DEPART_ID,
       B.DEVELOP_CITY_CODE,
       B.DEVELOP_EPARCHY_CODE,
       B.DEVELOP_NO,
       B.ASSURE_CUST_ID,
       B.ASSURE_TYPE_CODE,
       to_char(B.ASSURE_DATE, 'yyyy-mm-dd hh24:mi:ss') ASSURE_DATE,
       B.REMOVE_TAG,
       to_char(B.PRE_DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') PRE_DESTROY_TIME,
       to_char(B.DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') DESTROY_TIME,
       B.REMOVE_EPARCHY_CODE,
       B.REMOVE_CITY_CODE,
       B.REMOVE_DEPART_ID,
       B.REMOVE_REASON_CODE,
       to_char(B.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       B.UPDATE_STAFF_ID,
       B.UPDATE_DEPART_ID,
       B.REMARK,
       B.RSRV_NUM1,
       B.RSRV_NUM2,
       B.RSRV_NUM3,
       B.RSRV_NUM4,
       B.RSRV_NUM5,
       B.RSRV_STR1,
       B.RSRV_STR2,
       B.RSRV_STR3,
       B.RSRV_STR4,
       B.RSRV_STR5,
       B.RSRV_STR6,
       B.RSRV_STR7,
       B.RSRV_STR8,
       B.RSRV_STR9,
       B.RSRV_STR10,
       to_char(B.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
       to_char(B.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
       to_char(B.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,
       B.RSRV_TAG1,
       B.RSRV_TAG2,
       B.RSRV_TAG3,
       C.BRAND_CODE,
       C.PRODUCT_ID
  FROM TF_F_USER B, TF_F_USER_PRODUCT C
 WHERE B.PARTITION_ID = MOD(:USER_ID, 10000)
   AND B.USER_ID = :USER_ID
   AND C.USER_ID = :USER_ID
   AND B.USER_ID = C.USER_ID
   AND C.PARTITION_ID = MOD(:USER_ID, 10000)
