
SELECT t.discnt_rule_id,
       t.discnt_rule_name,
       t.eparchy_code,
       t.remark,
       t.state,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date
  FROM TD_B_DISCNT_RULE t
 WHERE (EPARCHY_CODE = :EPARCHY_CODE or :EPARCHY_CODE is null )
   and (t.state = :STATE or :STATE is null )
