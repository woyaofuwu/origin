INSERT INTO tf_f_relation_uu(partition_id,user_id_a,serial_number_a,user_id_b,serial_number_b,relation_type_code,role_code_a,role_code_b,orderno,short_code,start_date,end_date,update_time,inst_id)
SELECT mod(USER_ID_B,10000),USER_ID_A,SERIAL_NUMBER_A,USER_ID_B,SERIAL_NUMBER_B,relation_type_code,role_code_a,role_code_b,orderno,short_code,start_date,end_date,update_time,inst_id
  FROM tf_b_trade_relation
 WHERE trade_id = :TRADE_ID
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND modify_tag = '0'