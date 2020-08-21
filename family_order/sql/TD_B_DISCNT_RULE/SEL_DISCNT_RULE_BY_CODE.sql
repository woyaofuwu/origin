
SELECT t.discnt_rule_id,
       t.discnt_rule_name,
       t.discnt_rule_type,
       t.eparchy_code,
       t.remark,
       t.state,
       t.RULE_BIZ_ID,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,
       t.create_staff_id,
       t.create_depart_id,
       t.create_date,
       t.update_staff_id,
       t.update_depart_id,
       t.update_time
  FROM TD_B_DISCNT_RULE t
 WHERE discnt_rule_id = :DISCNT_RULE_ID 
