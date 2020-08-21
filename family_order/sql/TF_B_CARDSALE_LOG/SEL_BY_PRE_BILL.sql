SELECT value_card_no,value_card_type_code,value_code,factory_code,card_passwd,year_id,batch_no,contract_id,card_state_code,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,sale_tag,eparchy_code,city_code,stock_id,stock_id_o,staff_id,agent_id,stock_level,to_char(time_in,'yyyy-mm-dd hh24:mi:ss') time_in,staff_id_in,assign_staff_id,to_char(assign_time,'yyyy-mm-dd hh24:mi:ss') assign_time,to_char(sale_time,'yyyy-mm-dd hh24:mi:ss') sale_time,sale_staff_id,to_char(sale_money) sale_money,to_char(agent_fee) agent_fee,discount,fee_tag,to_char(active_time,'yyyy-mm-dd hh24:mi:ss') active_time,active_staff_id,to_char(log_id) log_id,to_char(oper_time,'yyyy-mm-dd hh24:mi:ss') oper_time,oper_staff_id,to_char(sale_log_id) sale_log_id,product_id, remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,rsrv_num1,rsrv_num2,rsrv_num3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5 
  FROM tf_r_valuecard
 WHERE (:SALE_TAG is null OR sale_tag=:SALE_TAG)
   AND eparchy_code=:EPARCHY_CODE
   AND (:CITY_CODE is null OR city_code=:CITY_CODE)
   AND (:STOCK_ID is null OR stock_id=:STOCK_ID)
   AND ( sale_log_id=TO_NUMBER(:SALE_LOG_ID_1) OR sale_log_id=TO_NUMBER(:SALE_LOG_ID_2) )