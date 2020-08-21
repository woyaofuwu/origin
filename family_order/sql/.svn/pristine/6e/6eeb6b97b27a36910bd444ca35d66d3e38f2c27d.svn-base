UPDATE tf_f_user_other
   SET end_date         = sysdate,
       rsrv_str1        = :RSRV_STR1,
       update_time      = sysdate,
       update_staff_id  = :UPDATE_STAFF_ID,
       update_depart_id = :UPDATE_DEPART_ID
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND user_id = TO_NUMBER(:USER_ID)
   AND rsrv_value_code = :RSRV_VALUE_CODE
   AND end_date > sysdate