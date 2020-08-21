UPDATE tf_b_trade
   SET oper_fee=oper_fee+TO_NUMBER(:OPER_FEE),fee_state='1',fee_time=NVL(fee_time,accept_date),fee_staff_id=NVL(fee_staff_id,trade_staff_id)
 WHERE trade_id=TO_NUMBER(:TRADE_ID)