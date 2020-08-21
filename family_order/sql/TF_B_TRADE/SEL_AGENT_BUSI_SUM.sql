SELECT a.depart_name ,a.depart_code,c.area_name,c.area_code,f.in_mode,e.trade_type,count(d.trade_id) SUMS
  FROM td_m_depart a,td_m_departkind b,td_m_area c,
      (SELECT * FROM tf_b_trade
      UNION all
      SELECT * FROM tf_bh_trade
where accept_month = to_number(to_char(to_date(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),'mm'))) d,td_s_tradetype e,td_s_inmode f
 WHERE a.depart_kind_code = b.depart_kind_code
   AND a.area_code = c.area_code
   AND a.depart_id = d.trade_depart_id
   AND d.trade_type_code = e.trade_type_code
   AND d.in_mode_code = f.in_mode_code
   AND d.trade_eparchy_code = f.eparchy_code
   AND a.depart_id = :DEPART_ID
   AND d.accept_date >= to_date(:START_DATE,'YYYY-MM-DD HH24:MI:SS')
   AND d.accept_date <  to_date(:END_DATE,'YYYY-MM-DD HH24:MI:SS')
 GROUP BY a.depart_code,a.depart_name,c.area_name,c.area_code,e.trade_type,f.in_mode