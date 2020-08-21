SELECT to_char(log_id) log_id,to_char(sub_log_id) sub_log_id,eparchy_code,city_code,res_type_code,res_kind_code,sale_type_code,to_char(sale_num) sale_num,start_value,end_value,stock_id,to_char(sale_money) sale_money,to_char(advance_pay) advance_pay,to_char(old_money) old_money,to_char(agent_fee) agent_fee,discount,pay_type_code,value_code,check_card_no,to_char(sale_time,'yyyy-mm-dd hh24:mi:ss') sale_time,sale_staff_id,sale_depart_id,PRODUCT_ID, remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_num1,rsrv_num2,rsrv_num3 
  FROM tf_b_cardsale_log
 WHERE eparchy_code=:EPARCHY_CODE
   and res_type_code=:RES_TYPE_CODE
   AND (:SUB_LOG_ID is null or sub_log_id=TO_NUMBER(:SUB_LOG_ID) )
   and (:LOG_ID is null or log_id=TO_NUMBER(:LOG_ID) )
   and sale_type_code=:SALE_TYPE_CODE
   and (:STOCK_ID is null or stock_id=:STOCK_ID)
   and (:SALE_DEPART_ID is null or sale_depart_id=:SALE_DEPART_ID)
   and (:SALE_STAFF_ID is null or sale_staff_id=:SALE_STAFF_ID)
   and (:START_VALUE is null or start_value<=:START_VALUE)
   and (:END_VALUE is null or end_value>=:END_VALUE)
   AND sale_time>=TO_DATE(:START_DATE,'yyyymmdd') 
   AND sale_time<=TO_DATE(:END_DATE,'yyyymmdd')+1
   and rownum<2