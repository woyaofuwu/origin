 SELECT a.depart_name ,a.depart_code,c.area_name,c.area_code,e.trade_type,count(d.trade_id) SUMS
  FROM td_m_depart a,td_m_area c,
      (SELECT * FROM tf_b_trade
      UNION all
      SELECT * FROM tf_bh_trade
where accept_month = to_number(to_char(to_date(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),'mm'))) d,td_s_tradetype e
 WHERE   a.depart_id = d.trade_depart_id
    AND a.area_code = c.area_code 
   AND d.trade_type_code = e.trade_type_code
   AND a.depart_id = :DEPART_ID
   AND d.accept_date >= to_date(:START_DATE,'YYYY-MM-DD HH24:MI:SS')
   AND d.accept_date <  to_date(:END_DATE,'YYYY-MM-DD HH24:MI:SS')
 GROUP BY a.depart_code,a.depart_name,c.area_name,c.area_code,e.trade_type