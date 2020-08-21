--IS_CACHE=Y
select integrate_item_code,detail_item_code,remark,eparchy_code,
 to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_depart_id,update_staff_id  
 from td_a_detailitemtointegrateitem where 
 integrate_item_code=:INTEGRATE_ITEM_CODE order by eparchy_code,detail_item_code