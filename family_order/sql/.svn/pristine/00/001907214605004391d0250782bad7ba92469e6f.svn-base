SELECT to_char(trade_id) trade_id,
       accept_month,
       fee_mode,
       fee_type_code,
       to_char(oldfee) oldfee,
       to_char(fee) fee,
       to_char(charge_id) charge_id,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR8,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       REMARK
  FROM tf_b_tradefee_sub
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND fee_mode = :FEE_MODE
UNION ALL
SELECT to_char(trade_id) trade_id,
       accept_month,
       fee_mode,
       fee_type_code,
       '0' oldfee,
       to_char(fee) fee,
       '' charge_id,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR8,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       REMARK
  FROM tf_b_tradefee_giftfee
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND fee_mode = :FEE_MODE