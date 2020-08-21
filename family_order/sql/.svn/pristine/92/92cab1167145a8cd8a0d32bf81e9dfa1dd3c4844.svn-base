SELECT to_char(a.log_id) log_id,a.eparchy_code,a.city_code,a.res_type_code,a.res_kind_code,a.sale_type_code,a.stock_id,to_char(sale_money/(a.sale_num*100)) sale_money,to_char(a.advance_pay/(a.sale_num*100)) advance_pay,a.pay_type_code,to_char(a.sale_time,'yyyy-mm-dd hh24:mi:ss') sale_time,a.product_id,a.sale_staff_id,a.sale_depart_id,b.res_no,b.serial_number,decode(b.back_tag,'0','未返单','1','已返单') back_tag
  FROM tf_b_cardsale_log a,tf_r_simcardorder b
 WHERE a.log_id=b.order_id 
 AND (a.sale_time >=TO_DATE(:START_DATE,'yyyy-mm-dd hh24:mi:ss')) 
 AND (a.sale_time <=TO_DATE(:END_DATE,'yyyy-mm-dd hh24:mi:ss')) 
 AND (:EPARCHY_CODE is null or a.eparchy_code =:EPARCHY_CODE) 
 AND (:CITY_CODE is null or a.city_code =:CITY_CODE) 
 AND (:START_VALUE is null or b.res_no>=:START_VALUE)
 AND (:END_VALUE is null or b.res_no<=:END_VALUE)
 AND (:START_VALUE is null or LENGTH(b.res_no) = LENGTH(:START_VALUE))  
 AND (:STOCK_ID is null or a.stock_id=:STOCK_ID) 
 AND (:RES_KIND_CODE is null or a.res_kind_code =:RES_KIND_CODE) 
 AND (:SALE_TYPE_CODE is null or a.sale_type_code =:SALE_TYPE_CODE) 
 AND (:SALE_STAFF_ID is null or a.sale_staff_id =:SALE_STAFF_ID)  
 AND (a.res_type_code =:RES_TYPE_CODE)
 AND (:BACK_TAG is null or b.back_tag =:BACK_TAG)