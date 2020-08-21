select o.rsrv_str8 || '|||' || replace(t.serial_number, 'KD_', '') || '|||' ||
       t.trade_depart_id || '|||' || d.depart_name || '|||' ||
       t.trade_city_code || '|||' || a.area_name || '|||' ||
       t.trade_staff_id || '|||' || f.staff_name || '|||' || t.trade_id ||
       '|||' || t.Trade_Type_Code || '|||' || e.TRADE_TYPE as FILEVALUE
  from ucr_crm1.tf_b_trade_other o,
       ucr_crm1.tf_bh_trade      t,
       td_m_depart               d,
       td_m_staff                f,
       td_m_area                 a,
       td_s_tradetype            e
 where o.accept_month = to_number(to_char(sysdate,'mm'))
   and o.rsrv_value_code = 'TOPSETBOX'
   and o.rsrv_str8 is not null
   and o.trade_id = t.trade_id
   and d.depart_id = t.trade_depart_id
   and f.staff_id = t.trade_staff_id
   and a.area_code = t.trade_city_code
   and t.trade_type_code = e.TRADE_TYPE_CODE
   and t.accept_date >= to_date(to_char(sysdate, 'yyyy-mm-dd hh24') || ':00:00', 'yyyy-mm-dd hh24:mi:ss') - 1 / 24
   and t.accept_date < to_date(to_char(sysdate, 'yyyy-mm-dd hh24') || ':00:00', 'yyyy-mm-dd hh24:mi:ss')
union all
select o.rsrv_str1 || '|||' || replace(t.serial_number, 'KD_', '') || '|||' ||
       t.trade_depart_id || '|||' || d.depart_name || '|||' ||
       t.trade_city_code || '|||' || a.area_name || '|||' ||
       t.trade_staff_id || '|||' || f.staff_name || '|||' || t.trade_id ||
       '|||' || t.Trade_Type_Code || '|||' || e.TRADE_TYPE as FILEVALUE
  from ucr_crm1.tf_b_trade_other o,
       ucr_crm1.tf_bh_trade      t,
       td_m_depart               d,
       td_m_staff                f,
       td_m_area                 a,
       td_s_tradetype            e
 where o.accept_month = to_number(to_char(sysdate,'mm'))
   and o.rsrv_value_code IN ('FTTH', 'FTTH_GROUP')
   and o.rsrv_str1 is not null
   and o.trade_id = t.trade_id
   and d.depart_id = t.trade_depart_id
   and f.staff_id = t.trade_staff_id
   and a.area_code = t.trade_city_code
   and t.trade_type_code = e.TRADE_TYPE_CODE
   and t.accept_date >= to_date(to_char(sysdate, 'yyyy-mm-dd hh24') || ':00:00', 'yyyy-mm-dd hh24:mi:ss') - 1 / 24
   and t.accept_date < to_date(to_char(sysdate, 'yyyy-mm-dd hh24') || ':00:00', 'yyyy-mm-dd hh24:mi:ss')
union all
select g.res_code || '|||' || t.rsrv_str1 || '|||' || t.trade_depart_id || '|||' ||
 d.depart_name || '|||' || t.trade_city_code || '|||' || a.area_name ||
 '|||' || t.trade_staff_id || '|||' || f.staff_name || '|||' || t.trade_id ||
 '|||' || t.Trade_Type_Code || '|||' || e.TRADE_TYPE as FILEVALUE
  from tf_bh_trade           t,
       tf_f_user_sale_active s,
       tf_f_user_sale_goods  g,
       td_m_depart           d,
       td_m_staff            f,
       td_m_area             a,
       td_s_tradetype        e
 where t.accept_month = to_number(to_char(sysdate,'mm'))
   and t.trade_type_code in (6900, 6800)
   and t.rsrv_str1 = s.serial_number
   and s.user_id = g.user_id
   and s.product_id = g.product_id
   and s.product_id = 66000245
   and d.depart_id = t.trade_depart_id
   and f.staff_id = t.trade_staff_id
   and a.area_code = t.trade_city_code
   and t.trade_type_code = e.TRADE_TYPE_CODE
   and t.accept_date >= to_date(to_char(sysdate, 'yyyy-mm-dd hh24') || ':00:00', 'yyyy-mm-dd hh24:mi:ss') - 1 / 24
   and t.accept_date < to_date(to_char(sysdate, 'yyyy-mm-dd hh24') || ':00:00', 'yyyy-mm-dd hh24:mi:ss')