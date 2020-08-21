INSERT INTO tf_f_user_discnt(partition_id,user_id,user_id_a,discnt_code,spec_tag,relation_type_code,start_date,end_date,update_time) 
SELECT TO_NUMBER(:PARTITION_ID),TO_NUMBER(:USER_ID),TO_NUMBER(:USER_ID_A),a.discnt_code,'2',:RELATION_TYPE_CODE,
  trunc(sysdate+1),a.end_date,sysdate
  FROM tf_f_user_discnt a,td_b_discnt b
 WHERE a.discnt_code = b.discnt_code
   and partition_id=MOD(TO_NUMBER(:USER_ID_A),10000)
   AND user_id=TO_NUMBER(:USER_ID_A)
   AND a.end_date < to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
   and b.rsrv_str5 is null
union all
SELECT TO_NUMBER(:PARTITION_ID),TO_NUMBER(:USER_ID),TO_NUMBER(:USER_ID_A),a.discnt_code,'2',:RELATION_TYPE_CODE,a.start_date,a.end_date
  FROM tf_f_user_discnt a,td_b_discnt b
 WHERE a.discnt_code = b.discnt_code
   and partition_id=MOD(TO_NUMBER(:USER_ID_A),10000)
   AND user_id=TO_NUMBER(:USER_ID_A)
   AND a.end_date >= to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
   and b.rsrv_str5 is null