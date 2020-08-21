INSERT INTO tf_b_trade_svc(trade_id,accept_month,user_id,user_id_a,product_id,package_id,service_id,main_tag,inst_id,campn_id,start_date,end_date,modify_tag,update_time,update_staff_id,update_depart_id,remark)     
SELECT TO_NUMBER(:TRADE_ID),TO_NUMBER(SUBSTRB(:TRADE_ID,5,2)),TO_NUMBER(:USER_ID),-1,TO_NUMBER(:PRODUCT_ID),a.package_id,b.service_id,a.main_tag,to_number(f_sys_getseqid(:EPARCHY_CODE,'seq_inst_id')) inst_id,'',to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss'),to_date('2050-12-31 23:59:59','yyyy-mm-dd hh24:mi:ss'),:MODIFY_TAG,SYSDATE,'','',''
  FROM td_b_package_element a, td_b_service b
  WHERE a.element_id = b.service_id
  AND a.element_type_code = 'S'
  AND a.package_id in (
    SELECT package_id 
     FROM td_b_product_package 
     WHERE product_id = TO_NUMBER(:PRODUCT_ID)
     AND (eparchy_code = :EPARCHY_CODE   Or eparchy_code='ZZZZ' )
     AND (force_tag = '1' OR default_tag = '1')
     AND SYSDATE > start_date
     AND SYSDATE < end_date
  )  
  and (a.force_tag = '1' OR a.default_tag = '1' Or a.main_tag='1')
  AND (SYSDATE BETWEEN a.start_date AND a.end_date)
  AND (SYSDATE BETWEEN b.start_date AND b.end_date)