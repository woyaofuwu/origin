INSERT INTO tf_f_user_discnt(partition_id,user_id,user_id_a,discnt_code,spec_tag,relation_type_code,start_date,end_date,update_time)
 select TO_NUMBER(:PARTITION_ID),TO_NUMBER(:USER_ID),TO_NUMBER(:USER_ID_A),discnt_code,'2',:RELATION_TYPE_CODE,start_date,end_date,sysdate
 from tf_b_trade_discnt
where trade_id = :TRADE_ID
  AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
  and id = :USER_ID
  and id_type = '1'
  and modify_tag = '0'