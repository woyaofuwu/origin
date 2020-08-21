INSERT INTO tf_f_relation_uu(partition_id,user_id_a,serial_number_a,user_id_b,serial_number_b,relation_type_code,role_code_a,role_code_b,orderno,short_code,start_date,end_date,update_time)
SELECT mod(a.id_b,10000),a.id_a,:SERIAL_NUMBER_A,a.id_b,b.Serial_Number,a.relation_type_code,a.role_code_a,a.role_code_b,a.orderno,a.short_code,a.start_date,a.end_date,a.update_time
  FROM tf_b_trade_relation a,tf_f_user b
 WHERE a.trade_id = :TRADE_ID
   AND a.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND a.modify_tag = '0'
   and b.user_id = a.id_b