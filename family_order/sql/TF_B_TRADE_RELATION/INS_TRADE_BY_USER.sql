INSERT INTO tf_b_trade_relation(trade_id,accept_month,relation_attr,relation_type_code,id_a,id_b,
       role_code_a,role_code_b,orderno,short_code,start_date,end_date,modify_tag,remark)
SELECT :TRADE_ID,substr(:TRADE_ID, 5, 2),'0',relation_type_code,to_char(user_id_a) user_id_a,to_char(user_id_b) user_id_b,
       role_code_a,role_code_b,orderno,short_code,start_date,SYSDATE,'1',''  
  FROM tf_f_relation_uu   
WHERE user_id_b = TO_NUMBER(:USER_ID_B)
  AND partition_id = MOD(TO_NUMBER(:USER_ID_B),10000)
  AND relation_type_code = :RELATION_TYPE_CODE 
  AND end_date > SYSDATE