SELECT partition_id,
       TO_NUMBER(:USER_ID) user_id,
         service_type,
         deal_flag,
         to_char(a.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
         to_char(a.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,
         b.param_code update_time
  FROM tf_f_user_svcswitch a, td_s_commpara b
 WHERE a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND a.user_id = TO_NUMBER(:USER_ID)
   AND b.subsys_code = 'CSM'
   AND b.param_attr = 934
   AND b.para_code1 = a.service_type
   AND SYSDATE BETWEEN b.Start_Date AND b.End_Date
   AND SYSDATE BETWEEN a.Start_Date AND a.End_Date
UNION ALL
 SELECT MOD(TO_NUMBER(:USER_ID),10000) partition_id,
        TO_NUMBER(:USER_ID) user_id,
        a.para_code1 service_type,
        '0' deal_flag,
        '' start_date,
        '' end_date,
        a.param_code update_time
   FROM td_s_commpara a
  WHERE a.subsys_code = 'CSM'
    AND a.param_attr = 934
    AND SYSDATE BETWEEN a.Start_Date AND a.End_Date
   AND NOT Exists(SELECT 1 FROM tf_f_user_svcswitch c 
                   WHERE c.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
                     AND c.user_id=TO_NUMBER(:USER_ID)
                     AND a.para_code1 = c.service_type                  
                     AND SYSDATE BETWEEN c.Start_Date AND c.End_Date
                     )