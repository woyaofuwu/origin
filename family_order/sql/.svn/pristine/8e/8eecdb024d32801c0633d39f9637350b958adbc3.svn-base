--IS_CACHE=Y
SELECT d.discnt_code,
       d.discnt_name,
       d.discnt_explain,
       d.c_discnt_code,
       d.b_discnt_code,
       d.a_discnt_code,
       d.obj_type_code,
       d.define_months,
       d.months,
       t.enable_tag,
       to_char(t.start_absolute_date, 'yyyy-mm-dd hh24:mi:ss') start_absolute_date,
       t.start_offset,
       t.start_unit,
       t.end_enable_tag,
       to_char(t.end_absolute_date, 'yyyy-mm-dd hh24:mi:ss') end_absolute_date,
       t.end_offset,
       t.end_unit,
       t.package_id,
       t.element_type_code,
       t.element_id,
       t.default_tag,
       t.force_tag,
       to_char(t.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(t.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,
       t.item_index,
       to_char(t.update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,
       t.update_staff_id,
       t.update_depart_id,
       t.cancel_tag,
       t.rsrv_tag3
  FROM ucr_cen1.td_b_package_element t, ucr_cen1.td_b_discnt d
 where 1 = 1
   and t.element_id = d.discnt_code
   and t.element_type_code = 'D'
   and nvl(t.rsrv_tag1,'0')<>'B'
   and sysdate between t.start_date and t.end_date
   and t.package_id = :PACKAGE_ID
   and (d.eparchy_code = :EPARCHY_CODE or d.eparchy_code = 'ZZZZ')
   and sysdate between d.start_date and d.end_date
