SELECT a.discnt_code as discnt_code,
       to_char(a.start_date, 'yyyy-mm-dd hh24:mi:ss') as start_date,
       to_char(a.end_date, 'yyyy-mm-dd hh24:mi:ss') as end_date,
       a.inst_id as inst_id,
       case
         when a.end_date > sysdate and sysdate > a.start_date then
          to_date(to_char(a.end_date, 'yyyy-mm-dd'), 'yyyy-mm-dd') -
          to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd')
         when a.end_date > sysdate and a.start_date > sysdate then
          to_date(to_char(a.end_date, 'yyyy-mm-dd'), 'yyyy-mm-dd') -
          to_date(to_char(a.start_date, 'yyyy-mm-dd'), 'yyyy-mm-dd')
         when a.end_date < sysdate then
          0
       end as leaving_days
  FROM tf_f_user_discnt a
 WHERE a.user_id = to_number(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
 AND A.End_Date >sysdate
 order by start_date