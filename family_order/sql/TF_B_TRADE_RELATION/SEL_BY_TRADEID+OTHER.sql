SELECT to_char(trade_id) trade_id,accept_month,relation_type_code,to_char(user_id_a) user_id_a,to_char(user_id_b) user_id_b,role_code_a,role_code_b,orderno,short_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,modify_tag,remark
  FROM tf_b_trade_relation
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND modify_tag = :MODIFY_TAG
   AND relation_type_code = :RELATION_TYPE_CODE
   AND role_code_b = :ROLE_CODE_B