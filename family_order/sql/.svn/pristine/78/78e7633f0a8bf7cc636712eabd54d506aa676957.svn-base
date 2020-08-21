 select t.* from TF_B_TRADE t,TF_B_TRADE_OTHER t1
 where t.trade_id = t1.trade_id
 and t1.rsrv_str4 = t.serial_number
 and t1.rsrv_str3=:KD_NUMBER
 and trade_type_code in ('6132','6133','6134','6135')