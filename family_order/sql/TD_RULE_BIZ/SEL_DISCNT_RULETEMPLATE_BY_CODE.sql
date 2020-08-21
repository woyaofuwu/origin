--IS_CACHE=Y
SELECT t.rule_biz_id,
       T.RULE_BIZ_DESC,
       t.eparchy_code,
       t.remark,
       t.rsrv_str3 state,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,
       t.RSRV_STR1,
       t.RSRV_STR5
  FROM TD_RULE_BIZ t
 WHERE rule_biz_id = :RULE_BIZ_ID
