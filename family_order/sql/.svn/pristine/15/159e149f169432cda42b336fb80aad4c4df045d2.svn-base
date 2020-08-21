--IS_CACHE=Y
SELECT BIZ_CODE,LIMIT_OBJ_B,OBJ_NAME,OBJ_TYPE,LIMIT_TYPE_CODE,to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE,EPARCHY_CODE,BRAND_CODE,PRODUCT_ID
  FROM TD_B_OPERATION_LIMIT
 WHERE obj_type = :OBJ_TYPE
   AND (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ')
   AND sysdate BETWEEN start_date AND end_date