--IS_CACHE=Y
SELECT t.rule_biz_id,
       t.rule_biz_desc,
       t.eparchy_code,
       t.remark,
       t.rsrv_str3 state,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date
  FROM TD_RULE_BIZ t
 WHERE  t.rule_biz_type_code='DiscntRuleCheck'
 and t.rule_biz_kind_code='DiscntRuleCheck'
 and t.rsrv_str3 != 4
