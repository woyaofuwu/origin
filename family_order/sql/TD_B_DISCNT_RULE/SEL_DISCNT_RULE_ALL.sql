
SELECT t.discnt_rule_id,
       t.discnt_rule_name,
       t.eparchy_code,
       t.remark,
       t.state,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date
  FROM TD_B_DISCNT_RULE t
 WHERE (t.discnt_rule_id = :DISCNT_RULE_ID)
 and (EPARCHY_CODE = :EPARCHY_CODE or :EPARCHY_CODE is null )
 and (t.start_date=(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')) or :START_DATE is null )
 and (t.end_date=(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')) or :END_DATE is null)
 and (t.state = :STATE or :STATE is null )
