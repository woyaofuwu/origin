SELECT * FROM(
  SELECT 
    TRADE_ID,
    ACTIVITY_NUMBER,
    ACCEPT_MONTH,
    USER_ID,
    SERIAL_NUMBER,
    CITY_CODE,
    DEAL_FLAG,
    to_char(ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE,
    PRIZE_TYPE_CODE,
    EXEC_FLAG,
    to_char(EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') EXEC_TIME,
    to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
    UPDATE_STAFF_ID,
    UPDATE_DEPART_ID,
    RANDOM_NUM,
    RADIX,
    PRIZE_ODDS_1,
    PRIZE_ODDS_2,
    PRIZE_ODDS_3,
    PRIZE_ODDS_4,
    PRIZE_ODDS_5,
    PRIZE_ODDS_6,
    REMARK,
    REVC1,
    REVC2,
    REVC3,
    REVC4,
    REVC5
  FROM TF_B_TRADE_UECLOTTERY WHERE 1=1
  AND (USER_ID=:USER_ID OR :USER_ID IS NULL)
  AND (DEAL_FLAG=:DEAL_FLAG OR :DEAL_FLAG IS NULL)
  AND (EXEC_FLAG=:EXEC_FLAG OR :EXEC_FLAG IS NULL)
  AND (ACTIVITY_NUMBER=:ACTIVITY_NUMBER OR :ACTIVITY_NUMBER IS NULL)
  AND (PRIZE_TYPE_CODE=:PRIZE_TYPE_CODE OR :PRIZE_TYPE_CODE IS NULL)
  AND (SERIAL_NUMBER=:SERIAL_NUMBER OR :SERIAL_NUMBER IS NULL)
  AND (ACCEPT_DATE > TO_DATE(:BEGIN_DATE,'YYYY-MM-DD HH24:MI:SS') OR :BEGIN_DATE IS NULL)
  AND (ACCEPT_DATE < TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS') OR :END_DATE IS NULL)
  ORDER BY PRIZE_TYPE_CODE ASC,ACCEPT_DATE ASC
) WHERE 1=1
AND (ROWNUM <= :ROW_NUMBER OR :ROW_NUMBER IS NULL)