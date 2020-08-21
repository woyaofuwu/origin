select a.PARTITION_ID AS PARTITION_ID,
       to_char(a.USER_ID) AS USER_ID,
       a.INST_TYPE AS INST_TYPE,
       to_char(a.INST_ID) AS INST_ID,
       a.ATTR_CODE AS ATTR_CODE,
       a.ATTR_VALUE AS ATTR_VALUE,
       to_char(a.START_DATE, 'YYYY-MM-DD HH24:MI:SS') AS START_DATE,
       to_char(a.END_DATE, 'YYYY-MM-DD HH24:MI:SS') AS END_DATE
  from tf_f_user_attr a, tf_f_user_svc s
 where 1=1
 and 'S' = :ELEMENT_TYPE_CODE
 and a.inst_type = :ELEMENT_TYPE_CODE
   and a.inst_id = s.inst_id
   and a.user_id = s.user_id
   and a.end_date > sysdate
   and s.service_id = to_number(:ELEMENT_ID)
   and s.user_id = to_number(:USER_ID)
   and s.partition_id = mod(TO_NUMBER(:USER_ID), 10000)
   and a.partition_id = mod(TO_NUMBER(:USER_ID), 10000)
union
select a.PARTITION_ID AS PARTITION_ID,
       to_char(a.USER_ID) AS USER_ID,
       a.INST_TYPE AS INST_TYPE,
       to_char(a.INST_ID) AS INST_ID,
       a.ATTR_CODE AS ATTR_CODE,
       a.ATTR_VALUE AS ATTR_VALUE,
       to_char(a.START_DATE, 'YYYY-MM-DD HH24:MI:SS') AS START_DATE,
       to_char(a.END_DATE, 'YYYY-MM-DD HH24:MI:SS') AS END_DATE
  from tf_f_user_attr a, tf_f_user_discnt d
 where 1=1
and 'D' = :ELEMENT_TYPE_CODE
 and a.inst_type = :ELEMENT_TYPE_CODE
   and a.inst_id = d.inst_id
   and a.user_id = d.user_id
   and a.end_date > sysdate
   and d.Discnt_Code = to_number(:ELEMENT_ID)
   and d.user_id = to_number(:USER_ID)
   and d.partition_id = mod(TO_NUMBER(:USER_ID), 10000)
   and a.partition_id = mod(TO_NUMBER(:USER_ID), 10000)