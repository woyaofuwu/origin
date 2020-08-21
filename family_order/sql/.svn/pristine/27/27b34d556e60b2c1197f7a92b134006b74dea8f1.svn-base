UPDATE TM_O_UECLOTTERY
   SET PRIZE_1 = PRIZE_1 - DECODE(:PRIZE_TYPE_CODE, '1', 1, 0),
       PRIZE_2 = PRIZE_2 - DECODE(:PRIZE_TYPE_CODE, '2', 1, 0),
       PRIZE_3 = PRIZE_3 - DECODE(:PRIZE_TYPE_CODE, '3', 1, 0),
       PRIZE_4 = PRIZE_4 - DECODE(:PRIZE_TYPE_CODE, '4', 1, 0),
       PRIZE_5 = PRIZE_5 - DECODE(:PRIZE_TYPE_CODE, '5', 1, 0),
       PRIZE_6 = PRIZE_6 - DECODE(:PRIZE_TYPE_CODE, '6', 1, 0)
 WHERE ROWID = (SELECT ROWID
                  FROM (SELECT ROWID
                          FROM TM_O_UECLOTTERY A
                         WHERE DECODE(:PRIZE_TYPE_CODE,
                                      '1', PRIZE_1,
                                      '2', PRIZE_2,
                                      '3', PRIZE_3,
                                      '4', PRIZE_4,
                                      '5', PRIZE_5,
                                      '6', PRIZE_6,
                                      0) > 0
                           AND ACTIVITY_NUMBER = :ACTIVITY_NUMBER
                           AND (CITY_CODE = :CITY_CODE OR CITY_CODE = 'HNHN')
                           AND FIRE_DAY <= TO_DATE(SUBSTR(:ACCEPT_DATE, 0, 8), 'YYYYMMDD')
                           AND TRUNC(FIRE_DAY,'MM') = TO_DATE(SUBSTR(:ACCEPT_DATE, 0, 6), 'YYYYMM')
                         ORDER BY FIRE_DAY,CASE WHEN CITY_CODE = 'HNHN' THEN 1 ELSE 0 END)
                 WHERE ROWNUM = 1)
   AND DECODE(:PRIZE_TYPE_CODE,
              '1', PRIZE_1,
              '2', PRIZE_2,
              '3', PRIZE_3,
              '4', PRIZE_4,
              '5', PRIZE_5,
              '6', PRIZE_6,
              0) > 0