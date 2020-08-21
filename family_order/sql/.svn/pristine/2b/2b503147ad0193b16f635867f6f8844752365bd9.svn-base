--IS_CACHE=Y
SELECT trade_type_code,brand_code,product_id,limit_code,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,right_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_s_trade_speclimit
 WHERE trade_type_code=:TRADE_TYPE_CODE
   AND product_id=:PRODUCT_ID
   AND limit_code=:LIMIT_CODE
   AND eparchy_code=:EPARCHY_CODE