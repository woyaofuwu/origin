--IS_CACHE=Y
select integrate_item_code,integrate_item,eparchy_code,
 to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_depart_id,update_staff_id   
 from td_a_integrateitem 
 order by eparchy_code,integrate_item_code