SELECT to_char(log_id) log_id,in_date,city_code,stock_id,staff_id,ticket_type_code,capacity_type_code,eparchy_code,tax_no,oper_type_code,stock_level,in_tag,modify_tag,fee,to_char(total_num) total_num,to_char(apply_num) apply_num,to_char(cancel_num) cancel_num,to_char(oper_num) oper_num,oper_staff_id,oper_depart_id,to_char(oper_time,'yyyy-mm-dd hh24:mi:ss') oper_time,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_num1,rsrv_num2,rsrv_num3,open_num1,open_num2,open_num3,0 x_tag 
  FROM ts_a_ticket_staff
 WHERE staff_id=:STAFF_ID
   AND (:TICKET_TYPE_CODE is null or ticket_type_code=:TICKET_TYPE_CODE)
   AND (:CAPACITY_TYPE_CODE is null or capacity_type_code=:CAPACITY_TYPE_CODE)
   AND (:TIME_S is null or oper_time>=to_date(:TIME_S ,'yyyy-mm-dd'))
   AND (:TIME_E is null or oper_time<=to_date(:TIME_E+1 ,'yyyy-mm-dd'))