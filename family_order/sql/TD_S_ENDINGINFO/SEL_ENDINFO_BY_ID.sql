--IS_CACHE=Y
SELECT TRADE_TYPE_CODE,BUSINAME,ENDINGINFO,to_char(OPTDATE,'yyyy-mm-dd hh24:mi:ss') OPTDATE,OPTSTAFF,RSRV1,RSRV2,BRAND_CODE,PRODUCT_ID,CANCEL_TAG,EPARCHY_CODE
  FROM TD_S_ENDINGINFO
 WHERE TRADE_TYPE_CODE = :TRADE_TYPE_CODE
 AND (brand_code = :BRAND_CODE OR brand_code='ZZZZ')
 AND (product_id = :PRODUCT_ID OR product_id=-1)
 AND (eparchy_code = :EPARCHY_CODE OR eparchy_code='ZZZZ')