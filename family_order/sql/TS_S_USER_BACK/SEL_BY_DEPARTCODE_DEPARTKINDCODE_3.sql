SELECT 
       a.OPEN_DATE,
       e.TRADE_STAFF_ID,
       e.TRADE_DATE,
       a.STAT_MONTH,
       b.depart_code,
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
       A.VALUEN2  IS_EXTEND_TIME , 
       nvl(round((500 - a.card_fee) / 100, 2), 0) card_fee

  FROM ts_s_user_back a, td_m_depart b, td_m_departkind c , tl_b_userback_extend e 
 WHERE 1=1
   and A.VALUEN2='1'
   and a.open_date >= to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss')
   AND a.open_date <= (trunc(to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss')+1)-1/(24*3600))
   AND b.depart_code >= :START_AGENT_NO
   AND b.depart_code <= :END_AGENT_NO
   AND c.depart_kind_code LIKE '%' || :DEPART_KIND_CODE || '%'
   AND a.develop_depart_id = b.depart_id
   AND b.depart_kind_code = c.depart_kind_code
   AND e.user_id(+) = a.user_id
       order by a.sim_card_no desc
