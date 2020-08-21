SELECT REFUND_ID,
       TID,
       OID,
       RSP_TIME,
       decode(RSP_RESULT,'00','审核通过全额退款','01','审核通过，同意部分退款','02','审核不通过，不同意退款') RSP_RESULT,
       REFUND_FEE,
       MEMO,
       UPDATE_DEPART_ID,
       UPDATE_STAFF_ID,
       UPDATE_TIME,
       REMARK
  FROM TF_B_CTRM_REFUND_SUB
 WHERE refund_id = :REFUND_ID
       and RSP_TIME is null