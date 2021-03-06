INSERT INTO TF_B_TRADEFEE_DEFER
  (TRADE_ID,
   USER_ID,
   ACCEPT_MONTH,
   DEFER_CYCLE_ID,
   DEFER_ITEM_CODE,
   FEE_MODE,
   FEE_TYPE_CODE,
   MONEY,
   ACT_TAG,
   UPDATE_TIME,
   UPDATE_STAFF_ID,
   UPDATE_DEPART_ID,
   REMARK,
   RSRV_STR1,
   RSRV_STR2,
   RSRV_STR3,
   RSRV_STR4,
   RSRV_STR5,
   RSRV_STR6,
   RSRV_STR7,
   RSRV_STR8,
   RSRV_STR9,
   RSRV_STR10)
VALUES
  (TO_NUMBER(:TRADE_ID),
   TO_NUMBER(:USER_ID),
   TO_NUMBER(:ACCEPT_MONTH),
   :DEFER_ACYC_ID,
   :DEFER_ITEM_CODE,
   :FEE_MODE,
   TO_NUMBER(:FEE_TYPE_CODE),
   TO_NUMBER(:MONEY),
   :ACT_TAG,
   TO_DATE(:UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss'),
   :UPDATE_STAFF_ID,
   :UPDATE_DEPART_ID,
   :REMARK,
   :RSRV_STR1,
   :RSRV_STR2,
   :RSRV_STR3,
   :RSRV_STR4,
   :RSRV_STR5,
   :RSRV_STR6,
   :RSRV_STR7,
   :RSRV_STR8,
   :RSRV_STR9,
   :RSRV_STR10)