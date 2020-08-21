SELECT eparchy_code,
       to_char(derate_id) derate_id,
       to_char(user_id) user_id,
       partition_id,
       acyc_id,
       derate_mode,
       to_char(derate_fee) derate_fee,
       to_char(derate_per) derate_per,
       to_char(derate_time, 'yyyy-mm-dd hh24:mi:ss') derate_time,
       derate_eparchy_code,
       derate_city_code,
       derate_depart_id,
       derate_staff_id,
       derate_reason_code,
       remark
  FROM tf_a_deratelatefeelog
 WHERE eparchy_code = :EPARCHY_CODE AND user_id = TO_NUMBER(:USER_ID) AND
       partition_id = :PARTITION_ID AND
       derate_time >=
       decode(:X_TAG,
              1,
              TO_DATE(:BEGIN_TIME, 'YYYY-MM-DD HH24:MI:SS'),
              0,
              add_months(TO_DATE(:END_TIME, 'YYYY-MM-DD HH24:MI:SS'),
                         :LIMIT_TIME)) and
       derate_time <= TO_DATE(:LIMIT_TIME_END, 'YYYY-MM-DD HH24:MI:SS')