--IS_CACHE=Y
SELECT d.DISCNT_GIFT_ID,
       d.DISCNT_GIFT_NAME,
       d.DISCNT_GIFT_EXPLAIN,
       d.A_DISCNT_CODE,
       d.DEPOSIT_TYPE,
       d.GIFT_USE_TAG,
       d.MONTHS,
       d.MONEY,
       d.DEPOSIT_HIGH_VALUE,
       d.DEPOSIT_LOW_VALUE,
       d.PAYMENT_ID,
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
       t.remark,
       d.rsrv_str4,
       t.RSRV_STR1,
       t.rsrv_tag3
  FROM ucr_cen1.td_b_package_element t, ucr_cen1.TD_B_SALE_DEPOSIT d
 where 1 = 1
   and t.element_id = d.DISCNT_GIFT_ID
   and t.element_type_code = 'A'
   and t.package_id = :PACKAGE_ID
   and (d.eparchy_code = :EPARCHY_CODE or d.eparchy_code = 'ZZZZ')
   and sysdate between d.start_date and d.end_date
