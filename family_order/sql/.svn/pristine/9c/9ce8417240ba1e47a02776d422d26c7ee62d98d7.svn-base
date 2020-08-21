UPDATE tf_b_trade
   SET bpm_id = NVL(:BPM_ID,bpm_id)
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND cancel_tag = :CANCEL_TAG