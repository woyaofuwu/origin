SELECT partition_id,b.param_code user_id,service_type,deal_flag,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,b.param_code update_time 
  FROM tf_f_user_svcswitch a ,td_s_commpara b
 WHERE a.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND a.user_id=TO_NUMBER(:USER_ID)
   AND b.subsys_code = 'CSM'
   AND b.param_attr = 934
   AND b.para_code1 = a.service_type
   AND SYSDATE BETWEEN b.Start_Date AND b.End_Date
   AND a.start_date=TO_DATE(:ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS')
UNION ALL 
SELECT partition_id,b.param_name user_id,service_type,deal_flag,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,b.param_code update_time 
  FROM tf_f_user_svcswitch a,td_s_commpara b
 WHERE a.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND a.user_id=TO_NUMBER(:USER_ID)
   AND b.subsys_code = 'CSM'
   AND b.param_attr = 934
   AND b.para_code1 = a.service_type
   AND SYSDATE BETWEEN b.Start_Date AND b.End_Date
   AND a.end_date=TO_DATE(:ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS')-1/24/3600
   AND NOT Exists(SELECT 1 FROM tf_f_user_svcswitch c ,td_s_commpara d
                   WHERE c.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
                     AND c.user_id=TO_NUMBER(:USER_ID)
                     AND d.subsys_code = 'CSM'
                     AND d.param_attr = 934
                     AND d.para_code1 = c.service_type
                     AND SYSDATE BETWEEN d.Start_Date AND d.End_Date
                     AND c.start_date=TO_DATE(:ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS'))