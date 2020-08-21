SELECT to_char(trade_id) trade_id,accept_month,to_char(user_id) user_id,out_group_id,serial_number,to_char(user_id_b) user_id_b,serial_number_b,short_code,modify_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,rsrv_str1,rsrv_str2,rsrv_str3 
  FROM tf_b_trade_vpmnout
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(SUBSTR(:TRADE_ID,5,2))