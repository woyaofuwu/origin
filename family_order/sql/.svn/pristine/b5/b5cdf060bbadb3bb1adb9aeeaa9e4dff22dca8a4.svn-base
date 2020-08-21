INSERT INTO tf_b_trade_relation(trade_id,accept_month,relation_attr,relation_type_code,id_a,id_b,
       role_code_a,role_code_b,orderno,short_code,start_date,end_date,modify_tag,remark)
SELECT :TRADE_ID,substr(:TRADE_ID, 5, 2),'0',relation_type_code,to_char(user_id_a) user_id_a,to_char(user_id_b) user_id_b,
       role_code_a,role_code_b,orderno,short_code,start_date,SYSDATE,'1',''
  FROM tf_f_relation_uu a
WHERE a.user_id_b = TO_NUMBER(:USER_ID_B)
  AND a.partition_id = MOD(TO_NUMBER(:USER_ID_B),10000)
  AND a.end_date > SYSDATE
  AND EXISTS(SELECT 1 FROM tf_b_trade_discnt b,tf_f_user_discnt c
              WHERE b.trade_id = TO_NUMBER(:TRADE_ID)
                AND b.accept_month = TO_NUMBER(substr(:TRADE_ID,5,2))
                AND b.id_type = '1'
                AND b.modify_tag = '1'
                AND c.user_id = b.id
                AND c.user_id = TO_NUMBER(:USER_ID_B)
                AND c.partition_id = MOD(TO_NUMBER(:USER_ID_B),10000)
                AND c.spec_tag = '2'
                AND SYSDATE < c.end_date
                AND a.user_id_a = c.user_id_a
                AND a.relation_type_code = c.relation_type_code
                AND c.discnt_code = b.discnt_code )