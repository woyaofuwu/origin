--IS_CACHE=Y
SELECT d.item_id,d.item_name 
  FROM TD_B_COMPITEM c,TD_B_DETAILITEM d 
 WHERE c.item_id=:ITEM_ID
   AND c.sub_item_id=d.item_id