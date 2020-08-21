SELECT to_char(log_id) log_id,to_char(sub_log_id) sub_log_id,eparchy_code,city_code,res_type_code,res_kind_code,sale_type_code,to_char(sale_num) sale_num,start_value,end_value,stock_id,to_char(sale_money) sale_money,to_char(advance_pay) advance_pay,to_char(old_money) old_money,to_char(agent_fee) agent_fee,discount,pay_type_code,value_code,check_card_no,to_char(sale_time,'yyyy-mm-dd hh24:mi:ss') sale_time,sale_staff_id,sale_depart_id,product_id,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,
to_char(rownum)  rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,
rsrv_num1,rsrv_num2,rsrv_num3 
  FROM tf_b_cardsale_log
 WHERE eparchy_code=:EPARCHY_CODE
   AND (:CITY_CODE IS NULL OR (:CITY_CODE IS NOT NULL AND city_code=:CITY_CODE))
   AND (:RES_TYPE_CODE IS NULL OR (:RES_TYPE_CODE IS NOT NULL AND res_type_code=:RES_TYPE_CODE))
   AND (:RES_KIND_CODE IS NULL OR (:RES_KIND_CODE IS NOT NULL AND res_kind_code=:RES_KIND_CODE))
   AND (:SALE_TYPE_CODE IS NULL OR (:SALE_TYPE_CODE IS NOT NULL AND sale_type_code=:SALE_TYPE_CODE))
   AND (:START_VALUE IS NULL OR (:START_VALUE IS NOT NULL AND start_value<=:START_VALUE))
   AND (:END_VALUE IS NULL OR(:END_VALUE IS NOT NULL AND end_value>=:END_VALUE))
   AND (:STOCK_ID IS NULL OR (:STOCK_ID IS NOT NULL AND stock_id||''=:STOCK_ID))
   AND sale_time>=TO_DATE(:SALE_TIME_S, 'YYYY-MM-DD HH24:MI:SS')
   AND sale_time<=TO_DATE(:SALE_TIME_E, 'YYYY-MM-DD HH24:MI:SS')
   AND (:SALE_STAFF_ID IS NULL OR(:SALE_STAFF_ID IS NOT NULL AND sale_staff_id||''=:SALE_STAFF_ID))
   AND (:PARA_VALUE1 IS NULL OR(:PARA_VALUE1 IS NOT NULL AND log_id||''=:PARA_VALUE1))