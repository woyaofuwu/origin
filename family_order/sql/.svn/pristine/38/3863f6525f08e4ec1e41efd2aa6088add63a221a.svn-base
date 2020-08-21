INSERT INTO tf_f_user_svcswitch(partition_id,user_id,service_type,deal_flag,start_date,end_date,update_time)
SELECT MOD(TO_NUMBER(:USER_ID),10000),TO_NUMBER(:USER_ID),para_code1,:DEAL_FLAG,to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss'),to_date('2050-12-31 23:59:59','yyyy-mm-dd hh24:mi:ss'),SYSDATE
  FROM td_s_commpara
 WHERE param_attr = 934
   AND subsys_code = 'CSM'
   AND eparchy_code = 'ZZZZ'
   AND SYSDATE BETWEEN start_date AND end_date