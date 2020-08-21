SELECT to_char(trade_id) trade_id,accept_month,relation_type_code,to_char(USER_ID_A) USER_ID_A,to_char(USER_ID_B) USER_ID_B,role_code_a,role_code_b,orderno,short_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,modify_tag,remark,serial_number_b,
       serial_number_a,
       inst_id
  FROM tf_b_trade_relation
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND modify_tag = :MODIFY_TAG