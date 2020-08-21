SELECT 
     T.PARTITION_ID,
       TO_CHAR(T.USER_ID) USER_ID,
       TO_CHAR(T.USER_ID_A) USER_ID_A,
       T.PRODUCT_ID,
       T.PRODUCT_MODE,
       T.BRAND_CODE,
       TO_CHAR(T.INST_ID) INST_ID,
       TO_CHAR(T.CAMPN_ID) CAMPN_ID,
       TO_CHAR(T.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       TO_CHAR(T.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       TO_CHAR(T.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       T.UPDATE_STAFF_ID,
       T.UPDATE_DEPART_ID,
       T.REMARK,
       T.RSRV_NUM1,
       T.RSRV_NUM2,
       T.RSRV_NUM3,
       TO_CHAR(T.RSRV_NUM4) RSRV_NUM4,
       TO_CHAR(T.RSRV_NUM5) RSRV_NUM5,
       T.RSRV_STR1,
       T.RSRV_STR2,
       T.RSRV_STR3,
       T.RSRV_STR4,
       T.RSRV_STR5,
       TO_CHAR(T.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
       TO_CHAR(T.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
       TO_CHAR(T.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,
       T.RSRV_TAG1,
       T.RSRV_TAG2,
       T.RSRV_TAG3,MAIN_TAG
  FROM TF_F_USER_PRODUCT T
 where T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)
   AND T.USER_ID = :USER_ID
   AND T.END_DATE > START_DATE
   AND T.END_DATE > SYSDATE
   AND T.Start_Date < sysdate
   AND T.MAIN_TAG = '1'