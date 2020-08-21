INSERT INTO TF_F_USER_PLATSVC_BOOK
  (SUBSCRIBE_ID,
  TRADE_ID,
  ACCEPT_MONTH,
  USER_ID,
  PRODUCT_ID,
  PACKAGE_ID,
   SERVICE_ID,
   SERIAL_NUMBER,
   SP_CODE,
   BIZ_CODE,
   BIZ_TYPE_CODE,
   BIZ_STATE_CODE,
   ORG_DOMAIN,
   OPER_CODE,
   OPR_SOURCE,
   BILL_TYPE,
   PRICE,
   FIRST_DATE,
   FIRST_DATE_MON,
   GIFT_SERIAL_NUMBER,
   GIFT_USER_ID,
   START_DATE,
   END_DATE,
   BOOK_STATE,
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
   RSRV_STR6,
   RSRV_STR7,
   RSRV_STR8,
   RSRV_STR9,
   RSRV_STR10,
   RSRV_DATE1,
   RSRV_TAG1,
   RSRV_TAG2,
   RSRV_TAG3,
   PKGSEQ,
   UDSUM,
   INTF_TRADE_ID)
VALUES
  (:SUBSCRIBE_ID,
  :TRADE_ID,
  :ACCEPT_MONTH,
  :USER_ID,
  :PRODUCT_ID,
  :PACKAGE_ID,
   :SERVICE_ID,
   :SERIAL_NUMBER,
   :SP_CODE,
   :BIZ_CODE,
   :BIZ_TYPE_CODE,
   :BIZ_STATE_CODE,
   :ORG_DOMAIN,
   :OPER_CODE,
   :OPR_SOURCE,
   :BILL_TYPE,
   :PRICE,
   TO_DATE(:FIRST_DATE,'YYYY-MM-DD hh24:mi:ss'),
   TO_DATE(:FIRST_DATE_MON,'YYYY-MM-DD hh24:mi:ss'),
   :GIFT_SERIAL_NUMBER,
   :GIFT_USER_ID,
   TO_DATE(:START_DATE, 'YYYY-MM-DD hh24:mi:ss'),
   TO_DATE(:END_DATE, 'YYYY-MM-DD hh24:mi:ss'),
   :BOOK_STATE,
   TO_DATE(:UPDATE_TIME, 'YYYY-MM-DD hh24:mi:ss'),
   :UPDATE_STAFF_ID,
   :UPDATE_DEPART_ID,
   :REMARK,
   :RSRV_NUM1,
   :RSRV_NUM2,
   :RSRV_NUM3,
   :RSRV_NUM4,
   :RSRV_NUM5,
   :RSRV_STR1,
   :RSRV_STR2,
   :RSRV_STR3,
   :RSRV_STR4,
   :RSRV_STR5,
   :RSRV_STR6,
   :RSRV_STR7,
   :RSRV_STR8,
   :RSRV_STR9,
   :RSRV_STR10,
   TO_DATE(:RSRV_DATE1, 'YYYY-MM-DD hh24:mi:ss'),
   :RSRV_TAG1,
   :RSRV_TAG2,
   :RSRV_TAG3,
   :PKGSEQ,
   :UDSUM,
   :INTF_TRADE_ID
   )