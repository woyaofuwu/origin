select  t.* ,a.rsrv_tag1
  from tf_b_trade t,tf_b_trade_other a
 where t.serial_number = 'KD_'||:SERIAL_NUMBER
   and t.trade_type_code in ('600','606') 
   and a.trade_id = t.trade_id
   and a.rsrv_value_code = 'FTTH'