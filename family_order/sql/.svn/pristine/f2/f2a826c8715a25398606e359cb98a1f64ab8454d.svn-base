
 select t.rule_biz_id, t.remark,t.discnt_rule_id,t.eparchy_code,t.discnt_rule_name,t.discnt_rule_type
  from TD_B_DISCNT_RULE t
 where t.eparchy_code = :EPARCHY_CODE
   and discnt_rule_type = :DISCNT_RULE_TYPE
   and (sysdate between t.start_date and t.end_date)
   and state='3'
