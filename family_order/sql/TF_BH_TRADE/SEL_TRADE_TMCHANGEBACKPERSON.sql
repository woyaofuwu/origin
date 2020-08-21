select (select e.rsrv_str1
          from ucr_crm1.tf_b_trade_other e
         where e.trade_id = r.trade_id
           and e.modify_tag in (1, 2)) || '|||' || r.rsrv_str1 || '|||' || replace(t.serial_number, 'KD_', '') || '|||' ||
       replace(t.serial_number, 'KD_', '') || '|||' || t.cust_name  || '|||' || t.trade_depart_id ||
       '|||' || d.depart_name  || '|||' || t.trade_staff_id || '|||' || f.staff_name || '|||' || t.trade_city_code || '|||' ||
       a.area_name || '|||' || to_char(t.accept_date, 'yyyymmddhh24miss') || '|||' || t.trade_id || '|||' || t.Trade_Type_Code || '|||' || e.TRADE_TYPE as FILEVALUE
  from ucr_crm1.tf_b_trade_other r,
       ucr_crm1.tf_bh_trade      t,
       td_m_depart               d,
       td_m_staff                f,
       td_m_area                 a,
       td_s_tradetype            e
 where t.trade_id in
       (select trade_id
          from (select o.trade_id, count(1)
                  from ucr_crm1.tf_b_trade_other o
                 where o.accept_month = to_number(to_char(sysdate,'mm'))
                   and o.rsrv_value_code IN ('FTTH', 'FTTH_GROUP')
                   and o.rsrv_str1 is not null
                 group by o.trade_id
                having count(1) = 2))
   and r.modify_tag = 0
   and r.accept_month = to_number(to_char(sysdate,'mm'))
   and r.rsrv_value_code IN ('FTTH', 'FTTH_GROUP')
   and r.rsrv_str1 is not null
   and r.trade_id = t.trade_id
   and d.depart_id = t.trade_depart_id
   and f.staff_id = t.trade_staff_id
   and a.area_code = t.trade_city_code
   and t.trade_type_code = e.TRADE_TYPE_CODE
   and t.accept_date >= to_date(to_char(sysdate, 'yyyy-mm-dd hh24') || ':00:00', 'yyyy-mm-dd hh24:mi:ss') - 1 / 24
   and t.accept_date < to_date(to_char(sysdate, 'yyyy-mm-dd hh24') || ':00:00', 'yyyy-mm-dd hh24:mi:ss')