UPDATE TM_O_LOTTERY
   SET STOCK_POS_1 = STOCK_POS_1 - DECODE(:PRIZE_TYPE_CODE, 1, 1, 0),
       ACTION_D1   = ACTION_D1 - DECODE(:PRIZE_TYPE_CODE, 2, 1, 0),
       ACTION_D2   = ACTION_D2 - DECODE(:PRIZE_TYPE_CODE, 3, 1, 0),
       ACTION_D3   = ACTION_D3 - DECODE(:PRIZE_TYPE_CODE, 4, 1, 0),
       STOCK_POS_2 = STOCK_POS_2 - DECODE(:PRIZE_TYPE_CODE, 5, 1, 0)
 WHERE FIRE_DAY =
       (SELECT MIN(FIRE_DAY)
          FROM TM_O_LOTTERY A
         WHERE DECODE(:PRIZE_TYPE_CODE,
                      1, STOCK_POS_1,
                      2, ACTION_D1,
                      3, ACTION_D2,
                      4, ACTION_D3,
                      5, STOCK_POS_2,
                      0) > 0
           AND CITY_CODE = DECODE(:PRIZE_TYPE_CODE, 5, :CITY_CODE, 'HNHN')
           AND FIRE_DAY <=
               TO_DATE(SUBSTR(:ACCEPT_DATE, 0, 8), 'yyyy-mm-dd')
           AND TO_CHAR(FIRE_DAY, 'yyyymm') = SUBSTR(:ACCEPT_DATE, 0, 6))
	AND CITY_CODE = DECODE(:PRIZE_TYPE_CODE, 5, :CITY_CODE, 'HNHN')