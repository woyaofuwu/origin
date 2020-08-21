DELETE FROM tf_f_user_discnt c
 WHERE c.user_id = TO_NUMBER(:USER_ID)
   AND c.partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND c.discnt_code IN (select to_number(a.para_code2)
                           from td_s_commpara a
                          where a.subsys_code = 'CSM'
                            and a.param_attr = 3700  and param_code not in ('98001901') )
   AND c.start_date > SYSDATE