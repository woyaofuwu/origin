delete from tf_b_trade_attr
where trade_id=to_number(:TRADE_ID)
  and accept_month=to_number(substr(:TRADE_ID,5,2))
  and user_id=to_number(:USER_ID)
  and inst_type=:INST_TYPE
  and inst_id=to_number(:INST_ID)
  and attr_code=:ATTR_CODE
  and modify_tag in ('4','5')