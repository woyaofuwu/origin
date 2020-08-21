--IS_CACHE=Y
SELECT A.YEAR_MONTH,
       DECODE(A.IN_DATE,
              0,
              '1个月内',
              1,
              '2-12个月',
              2,
              '13-24个月',
              3,
              '24个月以上',
              '不正确填写') IN_DATE,
       A.BADNESSINFO_SUM,
       TRIM(TO_CHAR(A.BADNESSINFO_SUM / B.SUM_COUNT * 100, '9990.99')) || '%' BADNESSINFO_ZB
  FROM (SELECT TO_CHAR(T.RECV_TIME, 'mm') YEAR_MONTH,
               (CASE
                 WHEN TRUNC(MONTHS_BETWEEN(SYSDATE, IN_DATE)) BETWEEN 0 AND 1 THEN
                  '0'
                 WHEN TRUNC(MONTHS_BETWEEN(SYSDATE, IN_DATE)) BETWEEN 2 AND 12 THEN
                  '1'
                 WHEN TRUNC(MONTHS_BETWEEN(SYSDATE, IN_DATE)) BETWEEN 13 AND 24 THEN
                  '2'
                 WHEN TRUNC(MONTHS_BETWEEN(SYSDATE, IN_DATE)) > 24 THEN
                  '3'
                 ELSE
                  '4'
               END) IN_DATE,
               SUM(1) BADNESSINFO_SUM
          FROM TF_F_BADNESS_INFO T
         WHERE 1 = 1
           AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) = :REPORT_TYPE_CODE OR
               :REPORT_TYPE_CODE IS NULL)
           AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR
               :REPORT_CODE IS NULL)
           AND (trunc(T.REPORT_TIME) >= TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR
               :REPORT_START_TIME IS NULL)
           AND (trunc(T.REPORT_TIME) <= TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR
               :REPORT_END_TIME IS NULL)
         GROUP BY TO_CHAR(T.RECV_TIME, 'mm'),
                  (CASE
                    WHEN TRUNC(MONTHS_BETWEEN(SYSDATE, IN_DATE)) BETWEEN 0 AND 1 THEN
                     '0'
                    WHEN TRUNC(MONTHS_BETWEEN(SYSDATE, IN_DATE)) BETWEEN 2 AND 12 THEN
                     '1'
                    WHEN TRUNC(MONTHS_BETWEEN(SYSDATE, IN_DATE)) BETWEEN 13 AND 24 THEN
                     '2'
                    WHEN TRUNC(MONTHS_BETWEEN(SYSDATE, IN_DATE)) > 24 THEN
                     '3'
                    ELSE
                     '4'
                  END)) A
  FULL OUTER JOIN (SELECT TO_CHAR(S.RECV_TIME, 'mm') YEAR_MONTH,
                          SUM(1) SUM_COUNT
                     FROM TF_F_BADNESS_INFO S
                    WHERE 1 = 1
                      AND (SUBSTR(S.SORT_RESULT_TYPE, 5, 2) =
                          :REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)
                      AND (SUBSTR(S.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR
                          :REPORT_CODE IS NULL)
                      AND (trunc(S.REPORT_TIME) >=
                          TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR
                          :REPORT_START_TIME IS NULL)
                      AND (trunc(S.REPORT_TIME) <=
                          TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR
                          :REPORT_END_TIME IS NULL)
                    GROUP BY TO_CHAR(S.RECV_TIME, 'mm')) B ON A.YEAR_MONTH =
                                                                   B.YEAR_MONTH
 ORDER BY A.YEAR_MONTH, A.IN_DATE