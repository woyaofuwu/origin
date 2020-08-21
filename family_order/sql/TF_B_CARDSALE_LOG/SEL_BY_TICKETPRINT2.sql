SELECT to_char(b.log_id) log_id,to_char(max(b.sub_log_id)) sub_log_id,max(b.eparchy_code) eparchy_code,max(b.city_code) city_code,max(b.res_type_code) res_type_code,max(b.res_kind_code) res_kind_code,max(b.sale_type_code) sale_type_code,to_char(sum(b.sale_num)) sale_num,min(b.start_value) start_value,max(b.end_value) end_value,max(b.stock_id) stock_id,to_char(sum(b.sale_money)) sale_money,to_char(sum(b.advance_pay)) advance_pay,to_char(sum(b.old_money)) old_money,to_char(sum(b.agent_fee)) agent_fee,max(b.discount) discount,max(b.pay_type_code) pay_type_code,max(b.value_code) value_code,max(b.check_card_no) check_card_no,to_char(max(b.sale_time),'yyyy-mm-dd hh24:mi:ss') sale_time,max(d.oper_staff_id) sale_staff_id,max(d.oper_depart_id) sale_depart_id,max(b.product_id) product_id,max(b.remark) remark,max(b.rsrv_tag1) rsrv_tag1,max(b.rsrv_tag2) rsrv_tag2,max(b.rsrv_tag3) rsrv_tag3,to_char(max(d.oper_date),'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(max(b.rsrv_date2),'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(max(b.rsrv_date3),'yyyy-mm-dd hh24:mi:ss') rsrv_date3,max(c.depart_kind_code) rsrv_str1,max(d.end_value1) rsrv_str2,max(d.rsrv_str1) rsrv_str3,max(b.rsrv_str4) rsrv_str4,max(b.rsrv_str5) rsrv_str5,max(b.rsrv_num1) rsrv_num1,max(b.rsrv_num2) rsrv_num2,max(b.rsrv_num3) rsrv_num3
  FROM tf_b_cardsale_log b, td_m_depart c, tf_b_manager_other_log d
 WHERE b.eparchy_code=:EPARCHY_CODE
   AND b.rsrv_str4 = d.log_id
   AND b.sale_type_code='5'
   AND d.oper_date>=TO_DATE(:SALE_TIME_S, 'YYYY-MM-DD HH24:MI:SS')
   AND d.oper_date<=TO_DATE(:SALE_TIME_E, 'YYYY-MM-DD HH24:MI:SS')
   AND (:LOG_ID is null or b.log_id=:LOG_ID)
   AND (:LOG_ID2 is null or d.log_id=:LOG_ID2)
   AND (:SALE_STAFF_ID is null or d.oper_staff_id=:SALE_STAFF_ID)
   AND (:SALE_DEPART_ID is null or d.oper_depart_id=:SALE_DEPART_ID)
   AND NOT EXISTS (select 1 from tf_b_cardsale_log c where c.sub_log_id=b.log_id and c.sale_type_code in ('3','4'))
   AND b.stock_id = c.depart_id
   GROUP BY b.log_id