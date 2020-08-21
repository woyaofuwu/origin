select to_char(max(trade_id)) trade_id
  from tf_bh_trade a
 where user_id = to_number(:USER_ID)
   and trade_type_code in (192, 7240)
   and cancel_tag = '0'
   and accept_month = to_number(to_char(sysdate, 'mm'))