select log_id, sub_log_id, eparchy_code, city_code, res_type_code, res_kind_code, sale_type_code, sale_num, start_value, end_value, stock_id, sale_money, advance_pay, old_money, agent_fee, discount, pay_type_code, value_code, check_card_no, sale_time, sale_staff_id, sale_depart_id, product_id, remark, rsrv_tag1, rsrv_tag2, rsrv_tag3, rsrv_date1, rsrv_date2, rsrv_date3, rsrv_str1, rsrv_str2, rsrv_str3, rsrv_str4, rsrv_str5, rsrv_num1, rsrv_num2, rsrv_num3                                       
from TF_B_CARDSALE_LOG
where  sale_time>=TO_DATE(:SALE_TIME_S,'YYYY-MM-DD HH24:MI:SS') and sale_time<=TO_DATE(:SALE_TIME_E, 'YYYY-MM-DD HH24:MI:SS')
and    ((:RES_NO_S is null and :RES_NO_E is null) or (:RES_NO_S<= start_value and :RES_NO_E>=end_value))
and    (:SALE_STAFF_ID is null or :SALE_STAFF_ID=sale_staff_id)
and    sale_type_code IN ( :SALE_TYPE_CODE1, :SALE_TYPE_CODEP, :SALE_TYPE_CODE4, :SALE_TYPE_CODES, :SALE_TYPE_CODER )