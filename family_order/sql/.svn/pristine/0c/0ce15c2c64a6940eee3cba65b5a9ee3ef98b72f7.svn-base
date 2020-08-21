--IS_CACHE=Y
select d.INTEGRATE_ITEM_CODE INTEGRATE_ITEM_CODE,
       c.DETAIL_ITEM_CODE DETAIL_ITEM_CODE,
       d.remark remark,
       to_char(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,
       c.update_depart_id update_depart_id,
       update_staff_id,
       c.eparchy_code eparchy_code
  from td_a_detailitemtointegrateitem c,
       (SELECT a.eparchy_code,
               a.note_item_code INTEGRATE_ITEM_CODE,
               a.note_item remark,
               b.INTEGRATE_ITEM_CODE update_depart_id
          FROM td_a_noteitem a, td_a_noteitem_det b
         WHERE a.eparchy_code = b.eparchy_code
           and a.flag = b.flag
           and a.note_item_code = b.note_item_code
           and a.FLAG = 0 and a.eparchy_code=:EPARCHY_CODE) d
 where c.integrate_item_code = to_number(d.update_depart_id) and c.eparchy_code=:EPARCHY_CODE