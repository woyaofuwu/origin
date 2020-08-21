SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,discnt_code,spec_tag,relation_type_code,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_discnt a
 WHERE a.user_id=TO_NUMBER(:USER_ID)
   AND a.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND NOT EXISTS(SELECT 1 FROM td_s_commpara b WHERE a.discnt_code=b.param_code
  AND b.subsys_code='CSM'
  AND b.param_attr='3320'
 AND b.end_date>SYSDATE)
 AND a.End_Date>SYSDATE