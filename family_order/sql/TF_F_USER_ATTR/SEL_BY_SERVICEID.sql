select a.PARTITION_ID AS PARTITION_ID, to_char(a.USER_ID) AS USER_ID, a.INST_TYPE AS INST_TYPE,
       to_char(a.INST_ID) AS INST_ID, a.ATTR_CODE AS ATTR_CODE, a.ATTR_VALUE AS ATTR_VALUE,
       to_char(a.START_DATE, 'YYYY-MM-DD HH24:MI:SS') AS START_DATE,a.RELA_INST_ID,
       to_char(a.END_DATE, 'YYYY-MM-DD HH24:MI:SS') AS END_DATE
 from   tf_f_user_attr a, tf_f_user_svc s
 where  a.inst_type = 'S'
 and    a.rela_inst_id = s.inst_id
 and    a.user_id = s.user_id
 and    a.end_date > sysdate
 and    s.service_id = to_number(:SERVICE_ID)
 and    s.user_id = to_number(:USER_ID)
 and    s.partition_id = mod(TO_NUMBER(:USER_ID),10000)
 and    a.partition_id = mod(TO_NUMBER(:USER_ID),10000)