DELETE FROM tf_b_testcard_use
 WHERE serial_number=:SERIAL_NUMBER
   AND apply_no=:APPLY_NO
   AND apply_batch_id=TO_NUMBER(:APPLY_BATCH_ID)
   AND (:CARD_STATE_CODE is null or card_state_code=:CARD_STATE_CODE)