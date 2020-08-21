select a.PARTITION_ID,
       to_char(a.USER_ID) USER_ID,
       a.MERCH_SPEC_CODE,
       a.PRODUCT_ORDER_ID,
       a.PRODUCT_OFFER_ID,
       a.PRODUCT_SPEC_CODE,
       a.PRODUCT_DISCNT_CODE,
       to_char(a.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       to_char(a.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       to_char(a.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       a.UPDATE_STAFF_ID,
       a.UPDATE_DEPART_ID,
       a.REMARK,
       a.RSRV_NUM1,
       a.RSRV_NUM2,
       a.RSRV_NUM3,
       to_char(a.RSRV_NUM4) RSRV_NUM4,
       to_char(a.RSRV_NUM5) RSRV_NUM5,
       a.RSRV_STR1,
       a.RSRV_STR2,
       a.RSRV_STR3,
       a.RSRV_STR4,
       a.RSRV_STR5,
       to_char(a.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
       to_char(a.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
       to_char(a.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,
       a.RSRV_TAG1,
       a.RSRV_TAG2,
       a.RSRV_TAG3,
       a.INST_ID
  from TF_F_USER_GRP_MERCHP_DISCNT a
 where 1 = 1
   and a.USER_ID = :USER_ID
   and (a.MERCH_SPEC_CODE = :MERCH_SPEC_CODE or :MERCH_SPEC_CODE IS NULL)
   and (a.PRODUCT_SPEC_CODE = :PRODUCT_SPEC_CODE or
       :PRODUCT_SPEC_CODE IS NULL)
   and (a.PRODUCT_DISCNT_CODE = :PRODUCT_DISCNT_CODE or
       :PRODUCT_DISCNT_CODE IS NULL)
   and a.end_date > sysdate
