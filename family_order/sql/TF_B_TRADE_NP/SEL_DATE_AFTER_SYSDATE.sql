SELECT TO_CHAR(SYSDATE + (TO_NUMBER(:TRADE_HOUR) * 3600 / 86400),
               'YYYY-MM-DD HH24:MI:SS') DEALTIME
  FROM DUAL