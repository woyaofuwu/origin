SELECT /*+ index(a PK_TF_F_USER_DISCNT) */ b.param_code,b.param_name,b.para_code1,b.para_code2,b.para_code3 from tf_f_user_discnt a,td_s_commpara b 
WHERE b.para_code1=a.discnt_code
      AND a.partition_id=MOD(to_number(:USER_ID),10000)
      AND a.user_id=to_number(:USER_ID)
      AND a.end_date>SYSDATE
      AND b.subsys_code='CSM'
      AND b.param_attr=966
      AND b.param_code=:PARAM_CODE
      AND b.para_code3=:PARA_CODE3
      AND b.end_date>SYSDATE