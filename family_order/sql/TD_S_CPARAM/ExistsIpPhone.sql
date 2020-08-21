select count(*) recordcount
  from tf_f_relation_uu
where   end_date < sysdate
  and   serial_number = :SERIAL_NUMBER