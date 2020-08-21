SELECT to_char(trade_id) trade_id,accept_month,relation_type_code,to_char(user_id_a) user_id_a,to_char(user_id_b) user_id_b,role_code_a,role_code_b,orderno,short_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,modify_tag,remark
 FROM TF_B_TRADE_RELATION t 
 WHERE t.accept_month=:ACCEPT_MONTH 
 AND t.serial_number_b=:SERIAL_NUMBER 
 AND t.user_id_b =:USER_ID
 AND t.relation_type_code='69'
 AND SYSDATE BETWEEN t.start_date AND t.end_date