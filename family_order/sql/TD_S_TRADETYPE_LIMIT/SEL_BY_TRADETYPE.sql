--IS_CACHE=Y
SELECT TRADE_TYPE_CODE,LIMIT_TRADE_TYPE_CODE,BRAND_CODE,LIMIT_ATTR,LIMIT_TAG,START_DATE,END_DATE,EPARCHY_CODE,REMARK,UPDATE_STAFF_ID,UPDATE_DEPART_ID,UPDATE_TIME FROM TD_S_TRADETYPE_LIMIT 
 WHERE TRADE_TYPE_CODE = :TRADE_TYPE_CODE 
   AND SYSDATE BETWEEN START_DATE AND END_DATE