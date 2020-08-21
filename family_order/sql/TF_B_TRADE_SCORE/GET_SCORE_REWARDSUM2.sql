SELECT rule_id,
         rule_name,
         sum(action_count) action_count,
         to_char(nvl(sum(value_changed) / 100, 0)) value_changed
    FROM (SELECT a.trade_id,
                          a.rule_id,
                          c.rule_name,
                          a.value_changed,
                          a.action_count
            FROM tf_b_trade_score    a,
                 tf_bh_trade   b,
                 td_b_exchange_rule  c,
                 td_m_depart         d
           WHERE a.trade_id = b.trade_id
             AND a.accept_month = b.accept_month
             and a.accept_month = to_number(substr(:START_DATE, 6, 2))
  			 and b.accept_month = to_number(substr(:START_DATE, 6, 2))
             AND a.rule_id = c.rule_id
             AND b.trade_depart_id = d.depart_id
             AND b.accept_date >=
                 to_date(:START_DATE, 'yyyy-mm-dd')
             AND b.accept_date <=
                 to_date(:END_DATE, 'yyyy-mm-dd')+1
             AND a.rule_id = TRIM(:RULE_ID)
             AND b.trade_city_code = TRIM(:TRADE_CITY_CODE)
             AND d.depart_kind_code = TRIM(:DEPART_KIND_CODE)
             AND b.trade_type_code = '330'
          ) 
   group by rule_id, rule_name