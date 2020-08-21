
UPDATE TD_RULE_BIZ
   SET start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),
   end_date = TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),
   remark =:REMARK,
   eparchy_code=:EPARCHY_CODE,
   update_time=SYSDATE
 WHERE rule_biz_id = :RULE_BIZ_ID
