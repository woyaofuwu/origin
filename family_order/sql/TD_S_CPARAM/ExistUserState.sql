SELECT COUNT(1) recordcount
 FROM tf_f_user
WHERE serial_number=:SERIAL_NUMBER
  AND remove_tag=:REMOVE_TAG