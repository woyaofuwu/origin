select p.PID,
       p.TID,
       p.OID,
       o.phone,
       o.RSRV_STR1 NEW_IMEI,
       p.CTRM_PRODUCT_ID,
       p.CTRM_PRODUCT_TYPE,
       p.CONTRACT_ID,
       p.PRODUCT_ID,
       p.PACKAGE_ID,
       p.ELEMENT_ID,
       p.ELEMENT_TYPE_CODE,
       p.STATUS,
       p.TRADE_ID,
       p.ACCEPT_DATE,
       p.ACCEPT_RESULT,
       p.ERROR_RESULT,
       p.UPDATE_TIME,
       p.UPDATE_STAFF_ID,
       p.UPDATE_DEPART_ID,
       p.REMARK,
       p.RSRV_STR1,
       p.RSRV_STR2,
       p.RSRV_STR3,
       p.RSRV_STR4,
       p.RSRV_STR5
  from TF_B_CTRM_ORDER_PRODUCT p,TF_B_CTRM_ORDER o
 WHERE p.OID =:OID
   and p.tid = :TID
   and p.CTRM_PRODUCT_TYPE = :CTRM_PRODUCT_TYPE
   and p.tid = o.tid
   and p.oid = o.oid