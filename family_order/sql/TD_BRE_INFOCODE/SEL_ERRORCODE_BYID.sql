--IS_CACHE=Y
--IS_CACHE=N
select code
  from td_bre_infocode a, td_bre_rule_manage b, td_bre_relation c
 where  c.rule_id = a.id
   and b.rule_biz_id = c.rule_biz_id
   and b.rule_check_mode=:RULE_CHECK_MODE
   and a.state=:STATE
   and a.id = :ID