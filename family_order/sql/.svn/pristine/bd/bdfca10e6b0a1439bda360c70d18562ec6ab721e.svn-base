INSERT INTO tf_b_trade_relation_uu_bak(trade_id,accept_month,partition_id,user_id_a,serial_number_a,user_id_b,inst_id,serial_number_b,relation_type_code,role_code_a,role_code_b,orderno,short_code,start_date,end_date,update_time)
SELECT to_NUMBER(:TRADE_ID),TO_NUMBER(SUBSTR(:TRADE_ID,5,2)),MOD(user_id_b,10000),user_id_a,serial_number_a,user_id_b,inst_id,serial_number_b,relation_type_code,role_code_a,role_code_b,orderno,short_code,TRUNC(SYSDATE),end_date,sysdate
  FROM tf_b_trade_relation_uu_bak
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND user_id_b = TO_NUMBER(:USER_ID)
   AND EXISTS (SELECT 1 FROM td_s_commpara a
                where a.subsys_code ='CSM'
                and a.param_attr = 6017  
                And a.param_code='1'
                and a.para_code1 = relation_type_code
                and a.end_date > sysdate
                )
   AND end_date > SYSDATE