select a.user_id user_id,
       a.discnt_code discnt_code,
       a.inst_id inst_id,
       to_char(a.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date
  from (select t.*
          from tf_f_user_discnt t
         where t.user_id in
               (select user_id
                  from tf_f_user
                 where net_type_code in ('11')
                   and remove_tag = '0'
                   and serial_number = :SERIAL_NUMBER)
           and t.end_date > sysdate) a
