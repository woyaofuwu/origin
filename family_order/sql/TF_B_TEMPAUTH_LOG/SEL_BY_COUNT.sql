select ask_staff_id, count(*)
  from tf_b_tempauth_log a
 where to_char(to_char(a.ask_time, 'YYYY-MM-DD')) = :DATE
 group by ask_staff_id
 having count(*) > :VALUE