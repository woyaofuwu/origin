SELECT a.discnt_code,
       c.para_code1,
       to_char(a.start_date, 'yyyy-mm-dd HH24:MI:SS'),
       to_char(a.end_date, 'yyyy-mm-dd HH24:MI:SS')
  FROM tf_f_user_discnt a, td_s_commpara c
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND user_id = TO_NUMBER(:USER_ID)
   AND c.subsys_code = 'CSM'
   AND c.param_attr = '6013'
   AND c.param_code = 'GPWP'
   AND c.para_code2 = to_char(a.discnt_code)
   AND sysdate BETWEEN c.start_date AND c.end_date
   AND c.eparchy_code = '0898'
   AND a.end_date > SYSDATE
