--IS_CACHE=Y
SELECT product_id,product_name,flag,to_number(product_id) note_item_code,product_name note_item,rsrv_info1,rsrv_info2,rsrv_num1,eparchy_code 
  FROM td_a_productitem
 WHERE eparchy_code =:EPARCHY_CODE and flag = :FLAG AND rsrv_info1=:RSRV_INFO1 ORDER BY product_id,note_item_code