--IS_CACHE=Y
SELECT ACTION_CODE paracode,ACTION_NAME paraname 
  FROM td_b_score_action
 WHERE EPARCHY_CODE=:TRADE_EPARCHY_CODE
   AND START_DATE<=SYSDATE
   AND END_DATE>=SYSDATE