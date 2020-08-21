SELECT '8899' INFO_CODE,
       DECODE(DISCNT_CODE,
              '8888',
              'XSMT',
              '8899',
              'XSMTC3',
              '8877',
              'XSMTC5') INFO_VALUE
  FROM TF_F_USER_DISCNT T
 WHERE T.USER_ID = :USER_ID
   AND DISCNT_CODE IN ('8888', '8877', '8899')
   AND T.START_DATE =
       (SELECT MAX(START_DATE) START_DATE
          FROM TF_F_USER_DISCNT T
         WHERE T.USER_ID = :USER_ID
           AND DISCNT_CODE IN ('8888', '8877', '8899'))