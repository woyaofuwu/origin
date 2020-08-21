SELECT to_char(trade_id) trade_id,to_char(bpm_id) bpm_id,to_char(token_id) token_id,deal_desc,trade_type_code,deal_depart_id,deal_staff_id,deal_role_id,to_char(deal_time,'yyyy-mm-dd hh24:mi:ss') deal_time,remak,rsrv_str1,rsrv_str2 
  FROM tf_b_orderlog
 WHERE trade_id=TO_NUMBER(:TRADE_ID)