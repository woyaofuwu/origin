SELECT to_char(trade_id) trade_id,SERIAL_NUMBER_B,accept_month,relation_type_code,role_code_a,role_code_b,orderno,short_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,modify_tag,remark 
  FROM tf_b_trade_relation
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND role_code_b='2'
   AND modify_tag in('0','1')