SELECT count(*) recordcount FROM tf_f_user_grpmbmp
   where serial_number= :SERIAL_NUMBER
         AND BIZ_STATE_CODE='A'
         AND BIZ_STATUS='A'
         AND END_DATE>SYSDATE