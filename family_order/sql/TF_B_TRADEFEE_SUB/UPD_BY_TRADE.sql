UPDATE tf_b_tradefee_sub a SET a.fee =to_number(:ADJUST_FEE)
 WHERE a.trade_id =:TRADE_ID  
 AND a.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
 AND a.fee_mode = '0'
 AND a.fee_type_code = :FEE_TYPE_CODE