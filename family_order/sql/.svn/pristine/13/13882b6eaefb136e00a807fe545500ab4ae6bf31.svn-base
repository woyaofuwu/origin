--IS_CACHE=Y
select distinct r.script_id
 from td_rule_script_release r, td_rule_biz b, td_rule_flow f, td_rule_def d
 where d.script_id = r.script_id
  and f.rule_id = d.rule_id
  and f.rule_biz_id = b.rule_biz_id
  and d.state = '0' 
  and sysdate between f.start_date and f.end_date
  and sysdate between b.start_date and b.end_date
  and r.state = '0'
  and r.script_type = 'GV'