--IS_CACHE=Y
SELECT a.product_id product_id,product_name,a.flag flag ,a.note_item_code note_item_code,note_item,a.rsrv_info1 rsrv_info1,a.rsrv_info2 rsrv_info2,
b.INTEGRATE_ITEM_CODE rsrv_num1,a.eparchy_code eparchy_code  
FROM td_a_productitem a, td_a_productitem_det b
WHERE  a.eparchy_code = b.eparchy_code(+) 
and a.flag = b.flag(+) 
and a.note_item_code = b.note_item_code(+) 
and a.FLAG=:FLAG
and a.product_id=b.product_id(+)
order by a.eparchy_code,a.product_id,a.note_item_code