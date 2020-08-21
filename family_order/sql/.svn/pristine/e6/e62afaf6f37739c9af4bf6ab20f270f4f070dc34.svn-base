SELECT TO_CHAR(a.operate_id) operate_id
  FROM tf_b_trade_batdeal a
 WHERE a.batch_id = TO_NUMBER(:BATCH_ID)
   AND a.deal_state <> '9'
   AND (:SERIAL_NUMBER IS NULL OR a.serial_number||NULL = :SERIAL_NUMBER)
   AND ROWNUM < 2