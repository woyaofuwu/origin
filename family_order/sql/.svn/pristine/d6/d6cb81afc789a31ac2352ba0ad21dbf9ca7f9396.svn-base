--IS_CACHE=Y
SELECT sp_code,biz_code,product_no,biz_name,biz_type,biz_type_code,biz_attr,biz_desc,access_mode,order_mode,count_side1,count_side2,bill_type,price,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,count_pct,num_time,num_day,biz_state_code,usage_desc,intro_url,biz_status,service_phone,contact,to_char(first_date,'yyyy-mm-dd hh24:mi:ss') first_date,foregift_type,foregift,opr_source,biz_process_tag,sms_process_tag,recognize_code,net_tag,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,file_name 
  FROM td_m_operation_sp
 WHERE sp_code=:SP_CODE
   AND biz_code=:BIZ_CODE
   AND biz_state_code=:BIZ_STATE_CODE
   AND biz_status=:BIZ_STATUS
   AND sysdate BETWEEN start_date AND end_date