    select t.*
  from tf_b_trade t,tf_b_trade_widenet b
 where t.trade_type_code in ('600','613')
   and t.cancel_tag = '0'
   and t.cust_id = :CUSTID_GROUP
   and t.serial_number = :KD_NUMBER
   and t.user_id = b.user_id
   and (b.rsrv_str2 = '3' or b.rsrv_str2 = '5')