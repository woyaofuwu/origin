--IS_CACHE=N
SELECT NVL(LAST_SCORE,0) 店员积分值,NVL(VERSION_ID,0) 版本号,NVL(LAST_SCORE,0) OLD_SCORE,NVL(VERSION_ID,0) VERSION_ID
  FROM CHNL_CU_BOOK_PARALLEL
 WHERE CUMU_ID =:CUMU_ID