SELECT to_char(log_id) log_id,start_res_no,end_res_no,eparchy_code,city_code,res_type_code,res_kind_code,sale_type_code,stock_id,to_char(sale_money) sale_money,to_char(old_money) old_money,to_char(sale_num) sale_num,value_code,to_char(sale_time,'yyyy-mm-dd hh24:mi:ss') sale_time,sale_staff_id,sale_depart_id,oper_tag,con_type_code,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_num1,rsrv_num2,rsrv_num3,0 x_tag
  FROM tf_b_chl_ressale_log
 WHERE eparchy_code=:EPARCHY_CODE
   and (:CITY_CODE is null or city_code=:CITY_CODE)
   and res_type_code=:RES_TYPE_CODE
   and (:STOCK_ID is null or stock_id=:STOCK_ID)
   AND sale_time>=TO_DATE(:START_DATE,'yyyy-mm-dd hh24:mi:ss') 
   AND sale_time<=TO_DATE(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
   and sale_staff_id=:SALE_STAFF_ID
   and (:START_VALUE is null or start_res_no>=:START_VALUE)
   and (:END_VALUE is null or end_res_no<=:END_VALUE)
   and (:OPER_TAG is null or oper_tag=:OPER_TAG)
   and (:LOG_ID is null or log_id=:LOG_ID)