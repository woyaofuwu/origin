 select t.user_id from TF_F_USER t,tf_f_user_discnt a
 where  t.user_state_codeset = '0' 
 and t.remove_tag = '0'
 and t.user_id = a.user_id
 and a.user_id not in(select user_id from tf_f_user_discnt where discnt_code in (select para_code1 from td_s_commpara where subsys_code='CSM' and param_attr='532' and param_code='600') and End_Date >sysdate)
 and a.discnt_code in (select para_code1 from td_s_commpara where subsys_code='CSM' and param_attr='532' and param_code='600')