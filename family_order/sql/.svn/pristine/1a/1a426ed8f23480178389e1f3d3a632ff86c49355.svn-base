SELECT to_char(log_id) log_id,to_char(sub_log_id) sub_log_id,eparchy_code,city_code,res_type_code,res_kind_code,sale_type_code,to_char(sale_num) sale_num,start_value,end_value,stock_id,to_char(sale_money/100) sale_money,to_char(advance_pay/100) advance_pay,to_char(old_money/100) old_money,to_char(agent_fee/100) agent_fee,discount,pay_type_code,value_code,check_card_no,to_char(sale_time,'yyyy-mm-dd hh24:mi:ss') sale_time,sale_staff_id,sale_depart_id,PRODUCT_ID, remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_num1,rsrv_num2,rsrv_num3 
  FROM tf_b_cardsale_log b
 WHERE eparchy_code=:EPARCHY_CODE
   and res_type_code=:RES_TYPE_CODE
   and (:LOG_ID is null or log_id=TO_NUMBER(:LOG_ID) )
   AND (:SUB_LOG_ID is null or sub_log_id=TO_NUMBER(:SUB_LOG_ID) )
   and sale_type_code=:SALE_TYPE_CODE
   and (:STOCK_ID is null or stock_id=:STOCK_ID)
   and (:SALE_STAFF_ID is null or sale_staff_id=:SALE_STAFF_ID)
   and (:START_VALUE is null or start_value<=:START_VALUE)
   and (:END_VALUE is null or end_value>=:END_VALUE)
   AND sale_time>=TO_DATE(:START_DATE,'yyyymmdd') 
   AND sale_time<=TO_DATE(:END_DATE,'yyyymmdd')+1
   AND rsrv_str5 is not NULL
   AND (:CONTRACT_ID IS NULL OR rsrv_str5=:CONTRACT_ID)
   AND b.sale_depart_id IN 
          (  SELECT depart_id FROM td_s_assignrule WHERE res_type_code=:RES_TYPE_CODE AND depart_frame  
                LIKE (
          SELECT c.depart_frame||'%'  FROM td_s_assignrule c , td_s_assignrule d
          WHERE ( c.depart_id=substr(d.depart_frame,length(d.depart_frame)-14,5)
           OR c.depart_id=substr(d.depart_frame,length(d.depart_frame)-9,5)
           OR c.depart_id=d.depart_id)
     AND c.res_type_code=d.res_type_code
     AND c.eparchy_code= d.eparchy_code
     AND c.res_type_code=:RES_TYPE_CODE
     AND d.depart_id=:SALE_DEPART_ID
     AND c.depart_kind_code='00Y') )