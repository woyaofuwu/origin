SELECT partition_id,to_char(user_id) user_id,service_type,deal_flag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM tf_f_user_svcswitch
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND SYSDATE BETWEEN start_date AND end_date
   AND service_type IN (SELECT para_code1 FROM td_s_commpara
                         WHERE subsys_code = 'CSM'
                           AND param_attr = 934
                           AND eparchy_code = 'ZZZZ'
                           AND SYSDATE BETWEEN start_date AND end_date
                           AND para_code2 = :PARA_CODE2)