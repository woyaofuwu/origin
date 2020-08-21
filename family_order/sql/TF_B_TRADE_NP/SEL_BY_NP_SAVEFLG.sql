Select COUNT(1) RECORDCOUNT
  from TF_B_TRADE_NP t
 where t.trade_type_code = '41'
   and t.rsrv_str4 = 'CANNOTSAVE'
   and t.accept_date > trunc(sysdate) - 10
   and t.serial_number = :SERIAL_NUMBER