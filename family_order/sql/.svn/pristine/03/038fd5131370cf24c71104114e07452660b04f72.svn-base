SELECT /*+ index(a PK_TF_F_USER_DISCNT) */ b.param_code,b.param_name,b.para_code1,b.para_code2,b.para_code3 from tf_f_user_discnt a,td_s_commpara b 
WHERE b.para_code1=a.discnt_code
      AND a.partition_id=MOD(to_number(:USER_ID),10000)
      AND a.user_id=to_number(:USER_ID)
      AND a.end_date>SYSDATE
      AND b.subsys_code='CSM'
      AND b.param_attr=966
      AND b.param_code='1'      
      AND b.end_date>SYSDATE
UNION
SELECT b.param_code,b.param_name,b.para_code1,b.para_code2,b.para_code3 from tf_f_user_svc a,td_s_commpara b
WHERE b.para_code1=a.service_id
      AND a.partition_id=MOD(to_number(:USER_ID),10000)
      AND a.user_id=to_number(:USER_ID)
      AND a.end_date>SYSDATE
      AND b.subsys_code='CSM'
      AND b.param_attr=966
      AND b.param_code='2'      
      AND b.end_date>SYSDATE
UNION
SELECT b.param_code,b.param_name,b.para_code1,b.para_code2,b.para_code3 from tf_f_user_platsvc a,td_s_commpara b
WHERE b.para_code1=a.service_id
      AND a.partition_id=MOD(to_number(:USER_ID),10000)
      AND a.user_id=to_number(:USER_ID)
      AND a.end_date>SYSDATE      
      AND (a.biz_state_code='A' OR a.biz_state_code='N' OR a.biz_state_code='L')
      AND b.subsys_code='CSM'
      AND b.param_attr=966
      AND b.param_code='3'      
      AND b.end_date>SYSDATE