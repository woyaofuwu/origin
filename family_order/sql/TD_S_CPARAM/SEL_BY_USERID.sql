SELECT COUNT(1) RECORDCOUNT
  FROM TF_B_TRADE A
 WHERE A.USER_ID = TO_NUMBER(:USER_ID)
   AND (:TRADE_TYPE_CODE = '3614' AND (TRADE_TYPE_CODE = '3614' OR (TRADE_TYPE_CODE != '3614' AND 1 = 2)) 
     OR :TRADE_TYPE_CODE != '3614'
     OR :TRADE_TYPE_CODE  IS NULL) 
   AND A.TRADE_TYPE_CODE NOT IN (2991, 3011, 3081, 2990, 3010, 3080)
   AND NOT EXISTS (SELECT 1 FROM TD_S_TRADETYPE_LIMIT T WHERE T.TRADE_TYPE_CODE = :TRADE_TYPE_CODE AND T.LIMIT_TRADE_TYPE_CODE = A.TRADE_TYPE_CODE AND T.LIMIT_TAG = '5')
 AND rownum < 2