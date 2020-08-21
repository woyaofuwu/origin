SELECT serial_number,user_id,to_char(pay_money) pay_money,sp_code,sp_name,channel_trade_id,rechannel_trade_id,to_char(comp_time,'yyyy-mm-dd hh24:mi:ss') comp_time,to_char(req_time,'yyyy-mm-dd hh24:mi:ss') req_time,sp_desc,rsrv_str1,rsrv_str2,rsrv_str3 
  FROM ti_bi_mo_feecash
 WHERE channel_trade_id=:CHANNEL_TRADE_ID