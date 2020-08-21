SELECT to_char(log_id) log_id,to_char(sub_log_id) sub_log_id,eparchy_code,city_code,res_type_code,res_kind_code,sale_type_code,to_char(sale_num) sale_num,start_value,end_value,stock_id,to_char(sale_money/100) sale_money,to_char(advance_pay/100) advance_pay,to_char(old_money/100) old_money,to_char(agent_fee/100) agent_fee,discount,pay_type_code,value_code,check_card_no,to_char(sale_time,'yyyy-mm-dd hh24:mi:ss') sale_time,sale_staff_id,sale_depart_id,product_id,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_num1,rsrv_num2,rsrv_num3 
 FROM tf_b_cardsale_log b
 WHERE b.eparchy_code=:EPARCHY_CODE
   AND (:CITY_CODE IS NULL OR b.city_code=:CITY_CODE)
   AND (:LOG_ID IS NULL or b.log_id=:LOG_ID)
   AND b.res_type_code=:RES_TYPE_CODE
   AND b.sale_type_code=:SALE_TYPE_CODE
   AND b.sale_time>=TO_DATE(:SALE_TIME_S, 'YYYY-MM-DD HH24:MI:SS')
   AND b.sale_time<=TO_DATE(:SALE_TIME_E, 'YYYY-MM-DD HH24:MI:SS')
   AND (:STOCK_ID IS NULL OR b.stock_id||''=:STOCK_ID)
   AND (:SALE_STAFF_ID IS NULL OR b.sale_staff_id||''=:SALE_STAFF_ID) 
   AND  not exists (select 1 from tf_b_cardsale_log c where c.sub_log_id=b.log_id and c.sale_type_code='4')