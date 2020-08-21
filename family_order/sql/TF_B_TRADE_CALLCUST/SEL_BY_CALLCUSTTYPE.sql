SELECT to_char(trade_id) trade_id,to_char(user_id) user_id,serial_number,cust_name,callcust_type_code,act_tag,reason,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,term_ip,trade_staff_id,trade_depart_id,trade_city_code,trade_eparchy_code,to_char(trade_time,'yyyy-mm-dd hh24:mi:ss') trade_time,remark 
  FROM tf_b_trade_callcust a
 WHERE callcust_type_code=:CALLCUST_TYPE_CODE 
AND trade_time>=TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS') 
AND trade_time<TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS')+1 
AND (:SERIAL_NUMBER IS NULL OR serial_number=:SERIAL_NUMBER)
AND (:ACT_TAG IS NULL OR act_tag=:ACT_TAG)
AND (:TRADE_STAFF_ID IS NULL OR trade_staff_id=:TRADE_STAFF_ID)
AND (:DEPART_ID IS NULL OR trade_depart_id=:DEPART_ID)
AND (:USER_TYPE_CODE IS NULL 
     OR EXISTS (SELECT 1 FROM tf_f_user_callcust b
                WHERE b.serial_number=a.serial_number
                AND b.callcust_type_code=a.callcust_type_code
                AND b.user_type_code=:USER_TYPE_CODE))