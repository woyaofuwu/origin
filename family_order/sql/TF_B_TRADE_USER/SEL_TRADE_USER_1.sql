select *
  from tf_b_trade_user t
  where t.trade_id=:TRADE_ID
  and   t.accept_month=TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
  and   t.modify_tag=:MODIFY_TAG