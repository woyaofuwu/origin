--IS_CACHE=Y
SELECT PRODUCT_ID, NAME, COLOR  FROM TD_S_TERMINAL_INFO_GRP
 WHERE 1=1
 AND (:PRODUCT_ID IS NULL OR PRODUCT_ID = :PRODUCT_ID)
 AND (:NAME IS NULL OR NAME like '%'||:NAME||'%')