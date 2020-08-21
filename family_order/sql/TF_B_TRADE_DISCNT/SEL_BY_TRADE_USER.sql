SELECT to_char(trade_id) trade_id,accept_month,user_id,discnt_code,modify_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(user_id_a) user_id_a,relation_type_code,to_char(inst_id) inst_id,to_char(campn_id) campn_id 
  FROM tf_b_trade_discnt
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND modify_tag=:MODIFY_TAG
   AND user_id=:USER_ID