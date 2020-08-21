UPDATE tf_b_trade_mbmp
   SET end_date = sysdate+3
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   and biz_type_code=:BIZ_TYPE_CODE
   and biz_state_code=:BIZ_STATE_CODE