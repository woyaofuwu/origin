SELECT N.STOCK_POS_1,
         N.ACTION_D1,
         N.ACTION_D2,
         N.ACTION_D3,
         M.STOCK_POS_2
    FROM (SELECT SUM(STOCK_POS_2) STOCK_POS_2
            FROM TM_O_LOTTERY
           WHERE 1 = 1
             AND (:START_DATE IS NULL OR
                 FIRE_DAY >= TO_DATE(:START_DATE, 'yyyy-mm-dd'))
             AND (:END_DATE IS NULL OR
                 FIRE_DAY <= TO_DATE(:END_DATE, 'yyyy-mm-dd'))
             AND CITY_CODE = :CITY_CODE) M,
         (SELECT SUM(STOCK_POS_1) STOCK_POS_1,
                 SUM(ACTION_D1) ACTION_D1,
                 SUM(ACTION_D2) ACTION_D2,
                 SUM(ACTION_D3) ACTION_D3
            FROM TM_O_LOTTERY
           WHERE 1 = 1
             AND (:START_DATE IS NULL OR
                 FIRE_DAY >= TO_DATE(:START_DATE, 'yyyy-mm-dd'))
             AND (:END_DATE IS NULL OR
                 FIRE_DAY <= TO_DATE(:END_DATE, 'yyyy-mm-dd'))) N