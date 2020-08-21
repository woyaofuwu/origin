SELECT COUNT(1) recordcount
  FROM tf_r_valuecard
 WHERE value_card_no=:VALUE_CODE
   AND value_card_type_code=:VALUE_CARD_TYPE_CODE
   AND eparchy_code=:EPARCHY_CODE
   AND (stock_id=:STOCK_ID or :STOCK_ID is null)
   AND (card_state_code = :CARD_STATE_CODE OR :CARD_STATE_CODE = '*' OR INSTR(:CARD_STATE_CODE,card_state_code) > 0)