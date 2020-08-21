UPDATE tf_f_user_svcswitch
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')-1/24/3600,
       update_time=SYSDATE  
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND service_type in (SELECT para_code1 FROM td_s_commpara
                      WHERE param_attr = 934
                        AND subsys_code = 'CSM'
                        AND eparchy_code = 'ZZZZ'
                        AND (para_code2 = :PARA_CODE2 or :PARA_CODE2 is null)
                        AND SYSDATE BETWEEN start_date AND end_date)
   AND SYSDATE BETWEEN start_date AND end_date