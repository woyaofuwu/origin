select replace(t.serial_number, 'KD_', '') || '|||' || t.user_id || '|||' ||
       t.cust_name || '|||' || t.trade_depart_id || '|||' || d.depart_name ||
       '|||' || t.trade_staff_id || '|||' || f.staff_name || '|||' ||
       t.trade_city_code || '|||' || a.area_name || '|||' ||
       to_char(t.accept_date, 'yyyymmddhh24miss') || '|||' || t.trade_id ||
       '|||' || w.rsrv_num1 || '|||' ||
       to_char(t.finish_date, 'yyyymmddhh24miss') || '|||' ||
       to_char(t.accept_date, 'yyyymmddhh24miss') || '|||' ||
       w.detail_address || '|||' || decode(e.TRADE_TYPE_CODE,600,'新装',6800,'新装',6900,'新装','移机') || '|||' || decode(e.TRADE_TYPE_CODE,600,'宽带',606,'宽带','固话') as FILEVALUE
  from tf_bh_trade                 t,
       ucr_crm1.tf_b_trade_widenet w,
       td_m_depart                 d,
       td_m_staff                  f,
       td_m_area                   a,
       td_s_tradetype              e
 where t.accept_month = to_number(to_char(sysdate,'mm'))
   and t.trade_type_code in (600,6800,6900，606)
   and d.depart_id = t.trade_depart_id
   and t.trade_id = w.trade_id
   and f.staff_id = t.trade_staff_id
   and a.area_code = t.trade_city_code
   and t.trade_type_code = e.TRADE_TYPE_CODE
   and t.cancel_tag = '3'
   and t.accept_date >= to_date(to_char(sysdate, 'yyyy-mm-dd hh24') || ':00:00', 'yyyy-mm-dd hh24:mi:ss') - 1 / 24
   and t.accept_date < to_date(to_char(sysdate, 'yyyy-mm-dd hh24') || ':00:00', 'yyyy-mm-dd hh24:mi:ss')
union all
select replace(t.serial_number, 'KD_', '') || '|||' || t.user_id || '|||' ||
       t.cust_name || '|||' || t.trade_depart_id || '|||' || d.depart_name ||
       '|||' || t.trade_staff_id || '|||' || f.staff_name || '|||' ||
       t.trade_city_code || '|||' || a.area_name || '|||' ||
       to_char(t.accept_date, 'yyyymmddhh24miss') || '|||' || t.trade_id ||
       '|||' || w.rsrv_num1 || '|||' ||
       to_char(t.finish_date, 'yyyymmddhh24miss') || '|||' ||
       to_char(t.accept_date, 'yyyymmddhh24miss') || '|||' ||
       w.detail_address || '|||' || decode(e.TRADE_TYPE_CODE,3800,'新装',4800,'新装','移机') || '|||' || '魔百盒' as FILEVALUE
  from tf_bh_trade                 t,
       tf_f_user_widenet           w,
       tf_f_user                   u,
       td_m_depart                 d,
       td_m_staff                  f,
       td_m_area                   a,
       td_s_tradetype              e
 where t.accept_month = to_number(to_char(sysdate,'mm'))
   and t.trade_type_code in (3800,4800)
   and d.depart_id = t.trade_depart_id
   and u.serial_number = 'KD_' || t.serial_number
   and u.remove_tag = '0'
   and u.user_id = w.user_id
   and f.staff_id = t.trade_staff_id
   and a.area_code = t.trade_city_code
   and t.trade_type_code = e.TRADE_TYPE_CODE
   and t.cancel_tag = '3'
   and t.accept_date >= to_date(to_char(sysdate, 'yyyy-mm-dd hh24') || ':00:00', 'yyyy-mm-dd hh24:mi:ss') - 1 / 24
   and t.accept_date < to_date(to_char(sysdate, 'yyyy-mm-dd hh24') || ':00:00', 'yyyy-mm-dd hh24:mi:ss')