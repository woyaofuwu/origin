update tf_f_sms_redmember
   set END_TIME = TO_DATE(:END_TIME, 'YYYY-MM-DD HH24:MI:SS')
 where serial_number = :SERIAL_NUMBER