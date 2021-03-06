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
   REMARK,
   RSRV_NUM2)
VALUES
  (:TRADE_ID,
   MOD(TO_NUMBER(:USER_ID), 10000),
   :USER_ID,
   :USER_ID_A,
   :RES_TYPE_CODE,
   :RES_CODE,
   :IMSI,
   :KI,
   :ARTIFICAL_SERICES_TAG,
   :RES_FEE,
   :DEVICE_COST,
   :PRODUCT_ID,
   :BASE_PACKAGE,
   :OPTIONAL_PACKAGE,
   :RES_BRAND_INFO,
   :WIDE_NET_ADDRESS,
   :INST_ID,
   TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),
   TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),
   TO_DATE(:UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS'),
   :UPDATE_STAFF_ID,
   :UPDATE_DEPART_ID,
   :REMARK,
   :RSRV_NUM2)
