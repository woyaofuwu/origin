INSERT INTO tf_f_user_discnt(partition_id,user_id,user_id_a,product_id,relation_type_code,inst_id,package_id,discnt_code,spec_tag,start_date,end_date,update_time,update_staff_id,update_depart_id)
SELECT MOD(a.user_id, 10000),a.user_id,user_id_a,product_id,relation_type_code,inst_id,package_id,discnt_code,spec_tag,start_date,end_date,sysdate,update_staff_id,update_depart_id
  FROM tf_b_trade_discnt a
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND modify_tag = '0'