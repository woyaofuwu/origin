--IS_CACHE=Y
SELECT trade_type_code,brand_code,product_id,service_id,old_state_code,new_state_code,eparchy_code FROM td_s_trade_svcstate
 WHERE trade_type_code=:TRADE_TYPE_CODE
 AND (brand_code = :BRAND_CODE OR brand_code = 'ZZZZ')
 AND (product_id = :PRODUCT_ID OR product_id = -1)
 AND (eparchy_code = :EPARCHY_CODE OR eparchy_code = 'ZZZZ')
 AND (old_state_code = '%' or old_state_code = :OLD_STATE_CODE)
 AND SERVICE_ID = :SERVICE_ID