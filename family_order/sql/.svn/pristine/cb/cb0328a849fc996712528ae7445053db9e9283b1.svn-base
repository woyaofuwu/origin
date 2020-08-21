--IS_CACHE=Y
SELECT trade_type_code,brand_code,product_id,service_id,open_tag,eparchy_code,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time
  FROM td_s_trade_svc
 WHERE trade_type_code=:TRADE_TYPE_CODE
   AND (brand_code=:BRAND_CODE OR brand_code='ZZZZ')
   AND (product_id=:PRODUCT_ID OR product_id=-1)
   AND (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ')