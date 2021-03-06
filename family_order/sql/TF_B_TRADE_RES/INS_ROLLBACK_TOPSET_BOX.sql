INSERT INTO TF_R_TOPSET_ROLLBACK
  (TRADE_ID,
   PARTITION_ID,
   USER_ID,
   USER_ID_A,
   RES_TYPE_CODE,
   RES_CODE,
   IMSI,
   KI,
   ARTIFICAL_SERICES_TAG,
   RES_FEE,
   DEVICE_COST,
   PRODUCT_ID,
   BASE_PACKAGE,
   OPTIONAL_PACKAGE,
   RES_BRAND_INFO,
   WIDE_NET_ADDRESS,
   INST_ID,
   START_DATE,
   END_DATE,
   UPDATE_TIME,
   UPDATE_STAFF_ID,
   UPDATE_DEPART_ID,
   REMARK)
  SELECT A.TRADE_ID,
         MOD(A.USER_ID,10000),
         A.USER_ID,
         A.USER_ID_A,
         A.RES_TYPE_CODE,
         A.RES_CODE,
         A.IMSI,
         A.KI,
         A.RSRV_NUM1,
         A.RSRV_NUM5,
         A.RSRV_NUM4,
         A.RSRV_STR1,
         A.RSRV_STR2,
         A.RSRV_STR3,
         A.RSRV_STR4,
         A.RSRV_STR5,
         A.INST_ID,
         A.START_DATE,
         A.END_DATE,
         A.UPDATE_TIME,
         A.UPDATE_STAFF_ID,
         A.UPDATE_DEPART_ID,
         A.REMARK
    FROM TF_B_TRADE_RES A
   WHERE A.TRADE_ID = :TRADE_ID
     AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))