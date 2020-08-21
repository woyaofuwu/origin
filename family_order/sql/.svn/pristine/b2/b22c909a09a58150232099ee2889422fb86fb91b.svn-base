select replace(t.serial_number, 'KD_', '') || '|||' || t.trade_depart_id ||
       '|||' || d.depart_name || '|||' || t.trade_staff_id || '|||' ||
       f.staff_name || '|||' || t.trade_city_code || '|||' || a.area_name ||
       '|||' || to_char(t.accept_date, 'yyyymmddhh24miss') || '|||' ||
       t.trade_id || '|||' || w.rsrv_num1 || '|||' ||
       to_char(t.finish_date, 'yyyymmddhh24miss') || '|||' ||
       to_char(t.accept_date, 'yyyymmddhh24miss') || '|||' ||
       w.old_detail_address || '|||' || w.detail_address || '|||' ||
       t.user_id || '|||' || t.cust_name || '|||' || '宽带' as FILEVALUE
  from ucr_crm1.tf_b_trade         t,
       ucr_crm1.tf_b_trade_widenet w,
       td_m_depart                 d,
       td_m_staff                  f,
       td_m_area                   a,
       td_s_tradetype              e
 where t.accept_month = to_number(to_char(sysdate,'mm'))
   and t.trade_type_code = 606
   and d.depart_id = t.trade_depart_id
   and t.trade_id = w.trade_id
   and f.staff_id = t.trade_staff_id
   and a.area_code = t.trade_city_code
   and t.trade_type_code = e.TRADE_TYPE_CODE
   and w.modify_tag = 0
   and t.accept_date >= to_date(to_char(sysdate, 'yyyy-mm-dd hh24') || ':00:00', 'yyyy-mm-dd hh24:mi:ss') - 1 / 24
   and t.accept_date < to_date(to_char(sysdate, 'yyyy-mm-dd hh24') || ':00:00', 'yyyy-mm-dd hh24:mi:ss')