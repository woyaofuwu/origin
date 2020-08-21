select partition_id,
       to_char(user_id) user_id, 
       to_char(user_id_a) user_id_a,
       inst_id,
       discnt_code,
       spec_tag,
       relation_type_code,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date
  from tf_f_user_discnt t
 where t.user_id = :USER_ID
   and t.user_id_a = :USER_ID_A 
   and t.discnt_code in ('1285', '1286', '1391')
   and t.start_date >= to_date(:DIVE_START_DATE, 'YYYY-MM-DD hh24:mi:ss')
   and t.start_date <= to_date(:DIVE_END_DATE, 'YYYY-MM-DD hh24:mi:ss')
union
select partition_id,
       to_char(user_id) user_id, 
       to_char(user_id_a) user_id_a,
       inst_id,
       discnt_code,
       spec_tag,
       relation_type_code,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date
  from tf_f_user_discnt t
 where t.user_id = :USER_ID
   and t.user_id_a = :USER_ID_A 
   and t.discnt_code in ('1285', '1286', '1391')
   and t.end_date >= to_date(:DIVE_START_DATE, 'YYYY-MM-DD hh24:mi:ss')
   and t.end_date <= to_date(:DIVE_END_DATE, 'YYYY-MM-DD hh24:mi:ss')