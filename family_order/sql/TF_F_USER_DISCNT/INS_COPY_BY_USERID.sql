INSERT INTO TF_F_USER_DISCNT
  (PARTITION_ID,
   USER_ID,
   USER_ID_A,
   PRODUCT_ID,
   PACKAGE_ID,
   DISCNT_CODE,
   SPEC_TAG,
   RELATION_TYPE_CODE,
   INST_ID,
   CAMPN_ID,
   START_DATE,
   END_DATE,
   UPDATE_TIME,
   UPDATE_STAFF_ID,
   UPDATE_DEPART_ID,
   REMARK,
   RSRV_NUM1,
   RSRV_NUM2,
   RSRV_NUM3,
   RSRV_NUM4,
   RSRV_NUM5,
   RSRV_STR1,
   RSRV_STR2,
   RSRV_STR3,
   RSRV_STR4,
   RSRV_STR5,
   RSRV_DATE1,
   RSRV_DATE2,
   RSRV_DATE3,
   RSRV_TAG1,
   RSRV_TAG2,
   RSRV_TAG3)
  SELECT PARTITION_ID,
         USER_ID,
         TO_NUMBER(:USER_ID_A),
         PRODUCT_ID,
         PACKAGE_ID,
         DISCNT_CODE,
         :SPEC_TAG,
         :RELATION_TYPE_CODE,
         TO_NUMBER(:INST_ID),
         CAMPN_ID,
         START_DATE,
         END_DATE,
         SYSDATE,
         :UPDATE_STAFF_ID,
         :UPDATE_DEPART_ID,
         REMARK,
         RSRV_NUM1,
         RSRV_NUM2,
         RSRV_NUM3,
         RSRV_NUM4,
         RSRV_NUM5,
         RSRV_STR1,
         RSRV_STR2,
         RSRV_STR3,
         RSRV_STR4,
         RSRV_STR5,
         RSRV_DATE1,
         RSRV_DATE2,
         RSRV_DATE3,
         RSRV_TAG1,
         RSRV_TAG2,
         RSRV_TAG3
    FROM TF_F_USER_DISCNT
   WHERE PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)
     AND USER_ID = TO_NUMBER(:USER_ID)
     AND DISCNT_CODE = :DISCNT_CODE
     AND SYSDATE BETWEEN START_DATE AND END_DATE