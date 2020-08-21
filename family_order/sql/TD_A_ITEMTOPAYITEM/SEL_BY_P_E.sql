--IS_CACHE=Y
SELECT payitem_code,eparchy_code,item_code,item_type_tag,priority,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_depart_id,update_staff_id 
  FROM td_a_itemtopayitem
 WHERE payitem_code=:PAYITEM_CODE
   AND eparchy_code=:EPARCHY_CODE
   ORDER BY item_code