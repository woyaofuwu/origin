SELECT eparchy_code,
       to_char(adjust_id) adjust_id,
       to_char(user_id) user_id,
       partition_id,
       acyc_id,
       adjust_type,
       adjust_mode,
       to_char(nvl(adjust_fee,'0')) adjust_fee,
      to_char(nvl(adjust_per,'0')) adjust_per,
       to_char(adjust_time, 'yyyy-mm-dd hh24:mi:ss') adjust_time,
       adjust_eparchy_code,
       adjust_city_code,
       adjust_depart_id,
       adjust_staff_id,
       adjust_reason_code,
       remark,
       operate_type,serial_number 
  FROM tf_a_adjustblog
 WHERE eparchy_code = :EPARCHY_CODE AND user_id = TO_NUMBER(:USER_ID) AND
       partition_id = :PARTITION_ID AND
       trim(adjust_staff_id) = :ADJUST_STAFF_ID AND
       adjust_time >=
       decode(:X_TAG,
              1,
              TO_DATE(:BEGIN_TIME, 'YYYY-MM-DD HH24:MI:SS'),
              0,
              add_months(TO_DATE(:END_TIME, 'YYYY-MM-DD HH24:MI:SS'),
                         :LIMIT_TIME)) and
       adjust_time <= TO_DATE(:LIMIT_TIME_END, 'YYYY-MM-DD HH24:MI:SS') and
       adjust_type = :ADJUST_TYPE