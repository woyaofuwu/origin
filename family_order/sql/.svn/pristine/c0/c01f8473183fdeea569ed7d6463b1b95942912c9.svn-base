SELECT /*+ first_rows */
eparchy_code,to_char(adjust_id) adjust_id,to_char(user_id) user_id,partition_id,to_char(bill_id) bill_id,acyc_id,adjust_type,adjust_mode,to_char(adjust_fee) adjust_fee,to_char(adjust_per) adjust_per,to_char(adjust_time,'yyyy-mm-dd hh24:mi:ss') adjust_time,adjust_eparchy_code,adjust_city_code,adjust_depart_id,adjust_staff_id,adjust_reason_code,remark,operate_type,sp_code,sp_class,return_kind,serial_number 
  FROM tf_a_infoadjustlog
 WHERE adjust_time >=
       decode(:X_TAG,
              1,
              TO_DATE(:BEGIN_TIME, 'YYYY-MM-DD HH24:MI:SS'),
              0,
              add_months(TO_DATE(:END_TIME, 'YYYY-MM-DD HH24:MI:SS'),:LIMIT_TIME)) 
and
       adjust_time <= TO_DATE(:LIMIT_TIME_END, 'YYYY-MM-DD HH24:MI:SS') AND
       trim(adjust_staff_id) = :ADJUST_STAFF_ID  and
       eparchy_code = :EPARCHY_CODE and
       adjust_type != :ADJUST_TYPE and sp_code=:SP_CODE
       AND operate_type=:OPERATE_TYPE