--IS_CACHE=Y
select d.INTEGRATE_ITEM_CODE INTEGRATE_ITEM_CODE,
       c.DETAIL_ITEM_CODE DETAIL_ITEM_CODE,
       d.integrate_item remark,
       to_char(c.update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,
       c.update_depart_id update_depart_id,
       c.update_staff_id update_staff_id,
       c.eparchy_code eparchy_code
  from td_a_detailitemtointegrateitem c, td_a_integrateitem  d
 where c.integrate_item_code = to_number(d.integrate_item_code) and d.EPARCHY_CODE=c.EPARCHY_CODE and  c.eparchy_code=:EPARCHY_CODE