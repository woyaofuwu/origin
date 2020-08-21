select * from tf_f_user_attr a
 where a.user_id = :USER_ID
   and a.inst_type = 'P'
   and a.attr_code = :ATTR_CODE_CENTREX_ID
   and (sysdate between a.start_date and a.end_date)
   and exists
   (select 1 from tf_f_user_attr ua 
     where ua.user_id = a.user_id
     and ua.inst_type = 'P'
     and ua.attr_code = :ATTR_CODE_AUDIO
     and ua.attr_value = '01'
     and (sysdate between ua.start_date and ua.end_date))