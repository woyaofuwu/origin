INSERT INTO tf_f_user_discnt(partition_id,user_id,user_id_a,discnt_code,spec_tag,relation_type_code,start_date,end_date,update_time)
 select TO_NUMBER(:PARTITION_ID),TO_NUMBER(:USER_ID),TO_NUMBER(b.user_id_a),discnt_code,'2',b.relation_type_code,a.start_date,a.end_date,sysdate
 from tf_b_trade_discnt a,tf_f_relation_uu b
where trade_id = :TRADE_ID
  AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
  and id = :USER_ID
  and b.user_id_b=:USER_ID
  and b.relation_type_code in (20,21)
  and trunc(last_day(sysdate ) + 1) between b.start_date and b.end_date
  and id_type = '1'
  and modify_tag = '0'
  and a.discnt_code=:DISCNT_CODE