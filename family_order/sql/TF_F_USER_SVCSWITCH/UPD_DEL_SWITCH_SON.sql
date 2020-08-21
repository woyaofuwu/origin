UPDATE tf_f_user_svcswitch
   SET end_date = TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')-1/24/3600,
       update_time = SYSDATE  
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id = TO_NUMBER(:USER_ID)
   AND SYSDATE BETWEEN start_date+0 AND end_date+0
   AND service_type NOT IN (SELECT para_code1 FROM td_s_commpara a,tf_f_user_plat_register b
                         WHERE a.param_attr = 934
                           AND a.subsys_code = 'CSM'
                           AND a.eparchy_code = 'ZZZZ'
                           AND a.para_code2 = '0'
                           AND SYSDATE BETWEEN a.start_date AND a.end_date
                           AND b.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
                           AND b.user_id = TO_NUMBER(:USER_ID)
                           AND a.param_code = b.biz_type_code
                           AND SYSDATE BETWEEN b.start_date+0 AND b.end_date+0)