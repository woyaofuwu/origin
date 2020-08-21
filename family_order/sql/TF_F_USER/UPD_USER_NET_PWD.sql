UPDATE tf_f_user
   SET rsrv_str5=:RSRV_STR5,rsrv_str1=:RSRV_STR1
 WHERE serial_number=:SERIAL_NUMBER
   AND remove_tag=:REMOVE_TAG