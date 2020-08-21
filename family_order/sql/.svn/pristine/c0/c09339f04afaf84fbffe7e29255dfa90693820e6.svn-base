--IS_CACHE=Y
SELECT integrate_item_code,detail_item_code,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_depart_id,update_staff_id,eparchy_code 
  FROM td_a_detailitemtointegrateitem
 WHERE integrate_item_code=:INTEGRATE_ITEM_CODE
   AND eparchy_code=:EPARCHY_CODE