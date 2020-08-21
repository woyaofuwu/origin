SELECT to_char(log_id) log_id,to_char(sub_log_id) sub_log_id,eparchy_code,city_code,res_type_code,res_kind_code,DECODE(sale_type_code,'1','售卡','4','返销','') sale_type_code,to_char(sale_num) sale_num,start_value,end_value,stock_id,to_char(sale_money / 100) sale_money,to_char(advance_pay / 100) advance_pay,to_char(old_money / 100) old_money,to_char(agent_fee / 100) agent_fee,discount,pay_type_code,value_code,check_card_no,to_char(sale_time,'yyyy-mm-dd hh24:mi:ss') sale_time,sale_staff_id,sale_depart_id,product_id,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_num1,rsrv_num2,rsrv_num3 
  FROM tf_b_cardsale_log
 WHERE eparchy_code=:EPARCHY_CODE
   AND sale_time>=TO_DATE(:RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss')
   AND sale_time<=TO_DATE(:RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') 
   AND (:CITY_CODE IS NULL OR city_code=:CITY_CODE)
   AND (:RES_TYPE_CODE IS NULL OR res_type_code=:RES_TYPE_CODE)
   AND (:SALE_TYPE_CODE IS NULL OR sale_type_code=:SALE_TYPE_CODE)
   AND (:STOCK_ID IS NULL OR stock_id=:STOCK_ID)