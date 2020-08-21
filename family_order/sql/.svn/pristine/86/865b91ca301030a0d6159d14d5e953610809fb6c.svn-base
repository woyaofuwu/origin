UPDATE tf_b_tradefee_giftfee
   SET charge_id=TO_NUMBER(:CHARGE_ID)  
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month= TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND fee_mode=:FEE_MODE
   AND fee_type_code=:FEE_TYPE_CODE