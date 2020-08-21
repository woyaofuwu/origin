DELETE FROM tf_f_user_other
 WHERE USER_ID = to_number(:USER_ID)
   AND RSRV_VALUE_CODE = :RSRV_VALUE_CODE
   AND RSRV_STR1 = :RSRV_STR1
   AND RSRV_STR3 = :RSRV_STR3
   AND START_DATE = to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss')
   AND rownum<2