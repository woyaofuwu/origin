UPDATE td_b_score_action
 SET   end_date=sysdate
WHERE  action_code=:ACTION_CODE
AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
AND eparchy_code=:EPARCHY_CODE