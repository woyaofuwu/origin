SELECT a.partition_id,to_char(a.user_id) user_id,to_char(a.user_id_a) user_id_a,a.discnt_code,a.spec_tag,a.relation_type_code,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_discnt a,td_s_commpara b
 WHERE to_char(a.discnt_code) = b.param_code
   AND b.param_attr = 355
   AND b.subsys_code = 'CSM'
   AND b.eparchy_code = '0898'
   AND a.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND a.user_id=TO_NUMBER(:USER_ID)
   AND a.end_date > sysdate
   AND b.end_date > sysdate