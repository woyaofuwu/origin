SELECT  to_char(trade_id) trade_id,accept_month,relation_type_code,to_char(USER_ID_A) USER_ID_A,to_char(USER_ID_B) USER_ID_B,INST_ID
role_code_a,role_code_b,orderno,short_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,
to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,modify_tag,remark FROM TF_B_TRADE_RELATION t 
WHERE t.serial_number_b=:SERIAL_NUMBER 
AND t.relation_type_code='69' 
AND t.USER_ID_B =:USER_ID
ORDER BY t.start_date DESC