select a.PARTITION_ID AS PARTITION_ID, to_char(a.USER_ID) AS USER_ID, a.INST_TYPE AS INST_TYPE,
       to_char(a.INST_ID) AS INST_ID, a.ATTR_CODE AS ATTR_CODE, a.ATTR_VALUE AS ATTR_VALUE,
       to_char(a.START_DATE, 'YYYY-MM-DD HH24:MI:SS') AS START_DATE,
       to_char(a.END_DATE, 'YYYY-MM-DD HH24:MI:SS') AS END_DATE
from   tf_f_user_attr a, tf_f_user_svc s
 where  a.inst_type = 'S'
 and    a.inst_id = s.inst_id
 and    a.user_id = s.user_id
 and    a.end_date > sysdate
 and    s.end_date+0 > sysdate
 and    s.service_id = to_number(:SERVICE_ID)
 and    a.attr_code = :ATTR_CODE
 and    a.attr_value= :ATTR_VALUE