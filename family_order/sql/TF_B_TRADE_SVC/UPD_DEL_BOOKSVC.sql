delete from tf_b_trade_svc
where trade_id=to_number(:TRADE_ID)
  and accept_month=to_number(substr(:TRADE_ID,5,2))
  and user_id=to_number(:USER_ID)
  and product_id=:PRODUCT_ID
  and service_id=:SERVICE_ID
  and modify_tag in ('4','5')