--IS_CACHE=Y
SELECT identity_check_mode,identity_check_name,trade_type_code,in_mode_code,brand_code,eparchy_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,remark 
  FROM td_s_trade_identity_check
 WHERE (trade_type_code=:TRADE_TYPE_CODE OR trade_type_code=-1)
   AND (in_mode_code=:IN_MODE_CODE OR in_mode_code='Z')
   AND (brand_code=:BRAND_CODE OR brand_code='ZZZZ')
   AND (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ')
   AND sysdate between start_date and end_date