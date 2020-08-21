 select t.* from tf_f_user_widenet t,tf_f_user b
  where t.rsrv_str2 = '3'
  and SYSDATE < t.end_date
  and t.user_id = b.user_id
  and b.serial_number = :SERIAL_NUMBER
  AND NOT EXISTS (SELECT A.USER_ID
          FROM tf_F_user_other a
         where a.user_id = t.user_id
           and a.rsrv_value_code = 'FTTH_GROUP'
           AND SYSDATE < A.END_DATE
           AND A.RSRV_STR3 = b.SERIAL_NUMBER)