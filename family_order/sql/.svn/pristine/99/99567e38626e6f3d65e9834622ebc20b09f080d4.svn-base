SELECT 
     sale_staff_id,sale_depart_id,
     sale_time RSRV_DATE1,
     DECODE(sale_type_code,'1','售卡','2','预售卡','5','售卡缴费','3','售卡退费','4','售卡返销','未知') sale_type_code, res_type_code,
     res_kind_code, value_code, discount,
     to_char(sum(sale_num)) sale_num,
     to_char(SUM(sale_money) / 100) sale_money
  FROM tf_b_cardsale_log
    WHERE eparchy_code=:EPARCHY_CODE
   AND sale_time>=TO_DATE(:RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss')
   AND sale_time<=TO_DATE(:RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') 
   AND (:RES_TYPE_CODE IS NULL OR res_type_code=:RES_TYPE_CODE)
   AND (:SALE_TYPE_CODE IS NULL OR sale_type_code=:SALE_TYPE_CODE)   
   AND (:STOCK_ID IS NULL OR SALE_DEPART_ID = :STOCK_ID)
   GROUP BY sale_staff_id, sale_depart_id,sale_time, SALE_TYPE_CODE, res_type_code,res_kind_code, value_code, discount