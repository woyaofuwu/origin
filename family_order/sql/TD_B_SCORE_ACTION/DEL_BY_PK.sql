DELETE FROM td_b_score_action
 WHERE action_code=:ACTION_CODE
   AND eparchy_code=:EPARCHY_CODE
   AND sysdate BETWEEN start_date AND end_date