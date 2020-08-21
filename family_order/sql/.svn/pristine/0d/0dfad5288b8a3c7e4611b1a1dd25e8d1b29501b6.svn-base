--IS_CACHE=Y
SELECT t.rule_biz_Id,
       t.rule_biz_desc,
       t.remark,
       t.eparchy_code,
       t.RSRV_STR3 state,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date
  FROM td_rule_biz t
 WHERE (EPARCHY_CODE = :EPARCHY_CODE or :EPARCHY_CODE is null )
   and (t.RSRV_STR3 = :STATE or :STATE is null )
   and t.rule_biz_type_code='DiscntRuleCheck'
   and t.rule_biz_kind_code='DiscntRuleCheck'
