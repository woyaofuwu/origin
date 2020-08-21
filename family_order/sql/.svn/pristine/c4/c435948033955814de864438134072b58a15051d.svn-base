UPDATE tf_f_user_other
   SET RSRV_DATE1  = TO_DATE(:RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS'),
       PROCESS_TAG = :PROCESS_TAG,
       RSRV_STR3   = :RSRV_STR3,
       RSRV_STR4   = :RSRV_STR4,
       RSRV_STR5   = :RSRV_STR5,
       RSRV_STR6   = :RSRV_STR6,
       RSRV_STR7   = :RSRV_STR7,
       RSRV_STR8   = :RSRV_STR8,
       RSRV_STR9   = :RSRV_STR9,
       RSRV_STR10   = :RSRV_STR10
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND user_id = TO_NUMBER(:USER_ID)
   AND rsrv_value_code = :RSRV_VALUE_CODE
   AND (rsrv_str1 = :RSRV_STR1 OR rsrv_str1 IS NULL)
   AND ((SYSDATE BETWEEN start_date AND end_date) OR
       (start_date > SYSDATE AND end_date > SYSDATE AND
       start_date < end_date))