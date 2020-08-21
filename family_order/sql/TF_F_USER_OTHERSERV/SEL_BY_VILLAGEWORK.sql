SELECT partition_id,to_char(user_id) user_id,service_mode,serial_number,process_info,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,process_tag,staff_id,depart_id,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,remark 
  FROM tf_f_user_otherserv a
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND service_mode=:SERVICE_MODE
   AND TRIM(process_tag)=:PROCESS_TAG
   AND sysdate<end_date
   AND NOT EXISTS(SELECT 1 FROM tf_b_trade_other b
                   WHERE trade_id=:TRADE_ID
                     AND rsrv_value_code=:SERVICE_MODE
                     AND rsrv_value=:USER_ID
                     AND a.serial_number=b.rsrv_str1
                     AND modify_tag='1')
UNION
SELECT mod(to_number(rsrv_value),10000) partition_id,rsrv_value user_id,rsrv_value_code service_mode,rsrv_str1 serial_number,'' process_info,0 rsrv_num1,0 rsrv_num2,0 rsrv_num3,'' rsrv_str1,'' rsrv_str2,'' rsrv_str3,'' rsrv_str4,'' rsrv_str5,'' rsrv_str6,'' rsrv_str7,'' rsrv_str8,'' rsrv_str9,'' rsrv_str10,'' rsrv_date1,'' rsrv_date2,'' rsrv_date3,'0' process_tag,'' staff_id,'' depart_id,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,'' remark
  FROM tf_b_trade_other
 WHERE trade_id=:TRADE_ID
   AND rsrv_value_code=:SERVICE_MODE
   AND rsrv_value=:USER_ID
   AND modify_tag='0'