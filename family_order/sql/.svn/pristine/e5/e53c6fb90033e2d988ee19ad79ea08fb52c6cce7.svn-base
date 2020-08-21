SELECT to_char(log_id) log_id,to_char(sub_log_id) sub_log_id,eparchy_code,city_code,res_type_code,res_kind_code,sale_type_code,to_char(sale_num) sale_num,start_value,end_value,stock_id,to_char(sale_money/100) sale_money,to_char(advance_pay/100) advance_pay,to_char(old_money/100) old_money,to_char(agent_fee/100)  agent_fee,discount,pay_type_code,value_code,check_card_no,to_char(sale_time,'yyyy-mm-dd hh24:mi:ss') sale_time,sale_staff_id,sale_depart_id,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_num1,rsrv_num2,rsrv_num3 
  FROM tf_b_cardsale_log 
 WHERE (:LOG_ID is null or log_id=TO_NUMBER(:LOG_ID)) 
 AND (:EPARCHY_CODE is null or eparchy_code =:EPARCHY_CODE) 
 AND (:CITY_CODE is null or city_code =:CITY_CODE) 
 AND (sale_time >=TO_DATE(:START_DATE,'yyyy-mm-dd hh24:mi:ss')) 
 AND (sale_time <=TO_DATE(:END_DATE,'yyyy-mm-dd hh24:mi:ss')) 
 AND (:START_VALUE is null or start_value>=:START_VALUE)
 AND (:START_VALUE is null or LENGTH(start_value) = LENGTH(:START_VALUE))  --add by xiangsy@20051222
 AND (:END_VALUE is null or end_value<=:END_VALUE) 
 AND (:STOCK_ID is null or stock_id=:STOCK_ID) 
 AND (:RES_KIND_CODE is null or res_kind_code =:RES_KIND_CODE) 
 AND (:SALE_TYPE_CODE is null or sale_type_code =:SALE_TYPE_CODE) 
 AND (:SALE_STAFF_ID is null or sale_staff_id =:SALE_STAFF_ID)  
 AND (res_type_code =:RES_TYPE_CODE)