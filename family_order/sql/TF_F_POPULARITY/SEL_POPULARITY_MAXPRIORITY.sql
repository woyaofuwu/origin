SELECT MAX(PRIORITY) MAX_PRIORITY
  FROM TF_F_POPULARITY
 WHERE 1 = 1
   AND POPULARITY_TYPE = :POPULARITY_TYPE
   AND POPULARITY_TRADE_TYPE = :POPULARITY_TRADE_TYPE