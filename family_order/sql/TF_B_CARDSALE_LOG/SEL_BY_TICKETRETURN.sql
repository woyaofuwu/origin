SELECT to_char(log_id) log_id,to_char(max(sub_log_id)) sub_log_id,max(eparchy_code) eparchy_code,max(city_code) city_code,max(res_type_code) res_type_code,max(res_kind_code) res_kind_code,max(sale_type_code) sale_type_code,to_char(sum(sale_num)) sale_num,min(start_value) start_value,max(end_value) end_value,max(stock_id) stock_id,to_char(sum(sale_money)) sale_money,to_char(sum(advance_pay)) advance_pay,to_char(sum(old_money)) old_money,to_char(sum(agent_fee)) agent_fee,max(discount) discount,max(pay_type_code) pay_type_code,max(value_code) value_code,max(check_card_no) check_card_no,to_char(max(sale_time),'yyyy-mm-dd hh24:mi:ss') sale_time,max(sale_staff_id) sale_staff_id,max(sale_depart_id) sale_depart_id,max(product_id) product_id,max(remark) remark,max(rsrv_tag1) rsrv_tag1,max(rsrv_tag2) rsrv_tag2,max(rsrv_tag3) rsrv_tag3,to_char(max(rsrv_date1),'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(max(rsrv_date2),'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(max(rsrv_date3),'yyyy-mm-dd hh24:mi:ss') rsrv_date3,max(rsrv_str1) rsrv_str1,max(rsrv_str2) rsrv_str2,max(rsrv_str3) rsrv_str3,max(rsrv_str4) rsrv_str4,max(rsrv_str5) rsrv_str5,max(rsrv_num1) rsrv_num1,max(rsrv_num2) rsrv_num2,max(rsrv_num3) rsrv_num3
  FROM tf_b_cardsale_log b
 WHERE b.eparchy_code=:EPARCHY_CODE
   AND (:CITY_CODE IS NULL OR b.city_code=:CITY_CODE)
   AND b.res_type_code=:RES_TYPE_CODE
   AND b.sale_type_code=:SALE_TYPE_CODE
   AND b.sale_time>=TO_DATE(:SALE_TIME_S, 'YYYY-MM-DD HH24:MI:SS')
   AND b.sale_time<=TO_DATE(:SALE_TIME_E, 'YYYY-MM-DD HH24:MI:SS')
   AND (:STOCK_ID IS NULL OR stock_id||''=:STOCK_ID)
   AND (:SALE_STAFF_ID IS NULL OR sale_staff_id||''=:SALE_STAFF_ID)
   and not exists (select 1 from tf_b_cardsale_log c where c.sub_log_id=b.log_id and c.sale_type_code='4')
   group by log_id