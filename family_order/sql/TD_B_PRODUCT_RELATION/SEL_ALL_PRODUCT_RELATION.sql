--IS_CACHE=Y
SELECT product_id,relation_type_code,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_b_product_relation
 WHERE ( product_id=:PRODUCT_ID OR :PRODUCT_ID IS NULL OR :PRODUCT_ID = -1 )
   AND ( relation_type_code=:RELATION_TYPE_CODE OR :RELATION_TYPE_CODE IS NULL )