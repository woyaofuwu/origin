--IS_CACHE=Y
SELECT d.SCORE_DEDUCT_ID,
       d.ID_TYPE,
       d.SCORE_TYPE_CODE,
       d.SCORE_VALUE,
       d.YEAR_ID,
       d.START_CYCLE_ID,
       d.END_CYCLE_ID,
       d.SCORE_VALUE_FEE,
       d.DEPOSIT_TAG,
       d.DEPOSIT_RATE,
       d.payment_id,
       d.DEDUCT_LOW,
       d.SCORE_LOW,
       d.MONTHS,
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
       t.remark
  FROM ucr_cen1.td_b_package_element t, ucr_cen1.TD_B_SALE_SCORE d
 where 1 = 1
   and t.element_id = d.SCORE_DEDUCT_ID
   and t.element_type_code = 'J'
   and t.package_id = :PACKAGE_ID
   and (d.eparchy_code = :EPARCHY_CODE or d.eparchy_code = 'ZZZZ')
   and sysdate between d.start_date and d.end_date
