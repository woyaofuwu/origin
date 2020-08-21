SELECT to_char(trade_id) trade_id,accept_month,relation_type_code,to_char(user_id_a) user_id_a,SERIAL_NUMBER_A,to_char(user_id_b) user_id_b,SERIAL_NUMBER_B,role_code_a,role_code_b,orderno,short_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,modify_tag,remark,RSRV_STR1,RSRV_STR2 
  FROM tf_b_trade_relation
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND relation_type_code = to_char(:RELATION_TYPE_CODE)