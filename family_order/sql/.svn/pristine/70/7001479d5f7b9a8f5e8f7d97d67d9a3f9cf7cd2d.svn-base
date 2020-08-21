--IS_CACHE=Y
select t.rule_biz_id, t.remark,t.RSRV_STR1,t.rule_biz_desc
  from TD_RULE_BIZ t
 where t.eparchy_code = :EPARCHY_CODE
 and t.rule_biz_type_code='DiscntRuleCheck'
 and t.rule_biz_kind_code='DiscntRuleCheck'
   and (sysdate between t.start_date and t.end_date)
   and t.rsrv_str3='3'
