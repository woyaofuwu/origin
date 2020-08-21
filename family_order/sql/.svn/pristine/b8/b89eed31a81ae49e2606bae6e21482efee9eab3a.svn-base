INSERT INTO tf_f_user_discnt(partition_id,user_id,user_id_a,product_id,relation_type_code,inst_id,package_id,discnt_code,spec_tag,start_date,end_date,update_time)
SELECT MOD(a.user_id, 10000),a.user_id,user_id_a,product_id,relation_type_code,inst_id,package_id,discnt_code,spec_tag,start_date,end_date,sysdate
  FROM tf_b_trade_discnt a
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND modify_tag = '0'
   AND NOT EXISTS (SELECT 1 FROM tf_f_user_discnt b
                    WHERE b.user_id = a.user_id
                      AND b.partition_id = MOD(a.user_id, 10000)
                      AND b.discnt_code = a.discnt_code
                      AND b.end_date > start_date
                      AND b.end_date > a.start_date)