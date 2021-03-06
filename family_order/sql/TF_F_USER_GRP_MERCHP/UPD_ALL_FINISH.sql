UPDATE TF_F_USER_GRP_MERCHP
   SET PRODUCT_OFFER_ID = :PRODUCT_OFFER_ID,
       GROUP_ID         = :GROUP_ID,
       SERV_CODE        = :SERV_CODE,
       BIZ_ATTR         = :BIZ_ATTR,
       STATUS           = :STATUS,
       END_DATE         = to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss'),
       UPDATE_TIME      = to_date(:UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss'),
       UPDATE_STAFF_ID  = :UPDATE_STAFF_ID,
       UPDATE_DEPART_ID = :UPDATE_DEPART_ID,
       REMARK           = :REMARK,
       RSRV_NUM1        = :RSRV_NUM1,
       RSRV_NUM2        = :RSRV_NUM2,
       RSRV_NUM3        = :RSRV_NUM3,
       RSRV_NUM4        = to_number(:RSRV_NUM4),
       RSRV_NUM5        = to_number(:RSRV_NUM5),
       RSRV_STR1        = :RSRV_STR1,
       RSRV_STR2        = :RSRV_STR2,
       RSRV_STR3        = :RSRV_STR3,
       RSRV_STR4        = :RSRV_STR4,
       RSRV_STR5        = :RSRV_STR5,
       RSRV_DATE1       = to_date(:RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss'),
       RSRV_DATE2       = to_date(:RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss'),
       RSRV_DATE3       = to_date(:RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss'),
       RSRV_TAG1        = :RSRV_TAG1,
       RSRV_TAG2        = :RSRV_TAG2,
       RSRV_TAG3        = :RSRV_TAG3,
       PRODUCT_ORDER_ID = :PRODUCT_ORDER_ID
 WHERE PARTITION_ID = MOD(to_number(:USER_ID), 10000)
   and USER_ID = to_number(:USER_ID)
   and MERCH_SPEC_CODE = :MERCH_SPEC_CODE
   and PRODUCT_SPEC_CODE = :PRODUCT_SPEC_CODE
   and START_DATE = to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss')