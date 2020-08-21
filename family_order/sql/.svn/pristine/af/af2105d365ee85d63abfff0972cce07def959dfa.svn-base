--IS_CACHE=Y
SELECT t.rule_biz_id,
       t.eparchy_code,
       t.remark,
       t.rsrv_str3 state,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date
  FROM TD_RULE_BIZ t
 WHERE (t.rule_biz_id = :RULE_BIZ_ID)
 and (EPARCHY_CODE = :EPARCHY_CODE or :EPARCHY_CODE is null )
 and (t.start_date=(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')) or :START_DATE is null )
 and (t.end_date=(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')) or :END_DATE is null)
 and (t.rsrv_str3 = :STATE or :STATE is null )
 and t.rule_biz_type_code='DiscntRuleCheck'
 and t.rule_biz_kind_code='DiscntRuleCheck'
