DELETE FROM TS_S_TRADE_DAY_STAFF A
 WHERE A.CLCT_DAY = :CLCT_DAY
   AND A.DEPART_ID >= :START_DEPART_ID
   AND A.DEPART_ID <= :END_DEPART_ID