SELECT 
     sale_staff_id,sale_depart_id,
     sale_time RSRV_DATE1,
     DECODE(sale_type_code,'5','缴费','3','退费','未知') sale_type_code,
     decode(res_type_code,'0','按号码','1','按sim卡','3','按有价卡','未知') res_type_code,
     to_char(SUM(sale_money) / 100) sale_money,
     count(log_id) RSRV_STR1 
  FROM tf_b_cardsale_log
    WHERE eparchy_code=:EPARCHY_CODE
   AND sale_time>=TO_DATE(:RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss')
   AND sale_time<=TO_DATE(:RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') 
   and sale_type_code in ('3','5')
   AND (:RES_TYPE_CODE IS NULL OR res_type_code=:RES_TYPE_CODE)
   AND (:SALE_TYPE_CODE IS NULL OR sale_type_code=:SALE_TYPE_CODE)   
   AND (:STOCK_ID IS NULL OR sale_depart_id=:STOCK_ID)
   GROUP BY sale_staff_id, sale_depart_id,sale_time,SALE_TYPE_CODE,res_type_code