
UPDATE TD_B_DISCNT_RULE
   SET start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),
   end_date = TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),
   discnt_rule_name =:DISCNT_RULE_NAME,
   eparchy_code=:EPARCHY_CODE,
   update_time=SYSDATE,
   remark=:REMARK
 WHERE discnt_rule_id = :DISCNT_RULE_ID
