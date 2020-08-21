SELECT a.serial_number,to_char(a.user_id) user_id,a.cust_name,to_char(a.in_time,'yyyy-mm-dd hh24:mi:ss') in_time,a.brand,to_char(a.last_stop_time,'yyyy-mm-dd hh24:mi:ss') last_stop_time,a.state_name,a.imsi,a.err_code,to_char(a.deal_time,'yyyy-mm-dd hh24:mi:ss') deal_time,to_char(a.value_n) value_n,to_char(a.value_d,'yyyy-mm-dd hh24:mi:ss') value_d,a.remark,to_char(b.leave_real_fee) value_s 
  FROM tf_f_user_operation_err a,tf_o_leaverealfee b
 WHERE a.user_id = b.user_id(+)
   AND (:ERR_CODE = -1 OR err_code=:ERR_CODE)
   AND deal_time >=TO_DATE(:START_DEAL_TIME, 'YYYY-MM-DD HH24:MI:SS')
   AND deal_time <TO_DATE(:END_DEAL_TIME, 'YYYY-MM-DD HH24:MI:SS')+1