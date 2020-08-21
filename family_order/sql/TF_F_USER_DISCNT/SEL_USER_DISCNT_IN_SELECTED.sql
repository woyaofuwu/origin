select
       a.discnt_code element_id,
       'D' element_type_code,
       '0' main_tag,
       to_char(a.start_date, 'yyyy-mm-dd') start_date,
       to_char(a.end_date, 'yyyy-mm-dd') end_date,
       a.inst_id,
       'exist' modify_tag
  from tf_f_user_discnt     a
 where a.user_id = to_number(:USER_ID)
   and a.partition_id = mod(to_number(:USER_ID), 10000)
   and a.end_date > sysdate
   and a.start_date <= a.end_date
