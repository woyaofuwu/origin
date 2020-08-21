SELECT b.depart_code,
       b.depart_name,
       c.depart_kind,
       a.serial_number,
       a.sim_card_no,
       TO_CHAR(a.valued1, 'yyyy-mm-dd HH24:MI') VALUED1,
       a.remark,
      a.sim_type_code,
       a.back_date,
       a.back_staff_id,
       a.tag,
       nvl(round((500 - a.card_fee) / 100, 2), 0) card_fee
  FROM ts_s_user_back a, td_m_depart b, td_m_departkind c
 WHERE a.stat_month >= :START_DATE
	 AND a.stat_month <= :END_DATE
   AND b.depart_code >= :START_AGENT_NO
   AND b.depart_code <= :END_AGENT_NO
   AND c.depart_kind_code LIKE '%' || :DEPART_KIND_CODE || '%'
   AND a.develop_depart_id = b.depart_id
   AND b.depart_kind_code = c.depart_kind_code
       order by a.sim_card_no desc
