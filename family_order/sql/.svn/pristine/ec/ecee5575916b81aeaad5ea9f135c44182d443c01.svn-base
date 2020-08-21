SELECT request_id,accept_month,serial_number,request_type_code,request_desc,request_pri,serv_item,to_char(dead_line,'yyyy-mm-dd hh24:mi:ss') dead_line,request_state,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,request_result,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,remark,trade_staff_id,trade_depart_id,trade_city_code,trade_eparchy_code 
  FROM tf_b_serv_request_info
 WHERE serial_number=:SERIAL_NUMBER
   AND (request_type_code=:REQUEST_TYPE_CODE or :REQUEST_TYPE_CODE = -1)
   AND start_date between TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')