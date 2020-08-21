SELECT to_char(a.user_id) user_id,
       partition_id,
       b.param_name service_type, 
       decode(a.deal_flag,'0','开','1','关') deal_flag,
       to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
       to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time
  FROM TF_F_USER_SVCSWITCH a,td_s_commpara b
  WHERE a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   and a.user_id = TO_NUMBER(:USER_ID)
   AND a.service_type = b.para_code1
   AND  b.subsys_code='CSM'
   AND  B.PARAM_ATTR='934'
   and sysdate between a.start_date and a.end_date