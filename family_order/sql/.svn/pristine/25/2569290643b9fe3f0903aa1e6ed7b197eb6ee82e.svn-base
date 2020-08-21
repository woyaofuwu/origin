INSERT INTO ti_b_user_discnt(partition_id,eparchy_code,city_code,user_id,user_id_a,discnt_code,spec_tag,relation_type_code,start_date,end_date,update_time)
SELECT a.partition_id,c.eparchy_code,c.city_code,a.user_id,a.user_id_a,a.discnt_code,a.spec_tag,a.relation_type_code,a.start_date,a.end_date,SYSDATE
  FROM tf_f_user_discnt a,tf_b_trade_discnt b,tf_b_trade c
 WHERE b.trade_id = TO_NUMBER(:TRADE_ID)
   AND b.trade_id = c.trade_id
   AND b.id = a.user_id
   AND a.spec_tag = '2'
   AND a.discnt_code=b.discnt_code