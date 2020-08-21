

SELECT o.TID,
       o.OID,
       o.OUT_OID,
       o.GOODS_ID,
       o.TITLE,
       o.NUM,
       o.PRICE,
       o.PROVINCE,
       o.CITY,
       o.TOTAL,
       o.ADJEST_FEE,
       o.ADJEST_REASON,
       o.MOBILE_NO,
       o.PHONE,
       t.distribution,
       o.USER_NAME,
       o.CERTIFICATE_TYPE,
       o.CERTIFICATE_NO,
       o.ORDER_STATUS,
       o.ACCEPT_DATE,
       o.ACCEPT_RESULT,
       o.ERROR_RESULT,
       o.RSP_TIME,
       o.UPDATE_TIME,
       o.UPDATE_STAFF_ID,
       o.UPDATE_DEPART_ID,
       o.REMARK,
       o.RSRV_STR1,
       o.RSRV_STR2,
       o.RSRV_STR3,
       o.RSRV_STR4,
       o.RSRV_STR5
  FROM TF_B_CTRM_ORDER o ,TF_B_CTRM_TLIST  t
 WHERE o.TID = :TID
       AND o.OID = :OID
       and o.tid = t.tid
