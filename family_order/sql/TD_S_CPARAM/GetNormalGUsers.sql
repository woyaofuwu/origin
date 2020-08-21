SELECT count(*) recordcount
  FROM tf_f_user
 WHERE serial_number = :PARAM0
   AND remove_tag = :PARAM1