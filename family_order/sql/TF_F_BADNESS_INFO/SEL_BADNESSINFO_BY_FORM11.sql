--IS_CACHE=Y
SELECT A.YEAR_MONTH,
       DECODE(A.ALLNEWROWE_FEE,
              0,
              '0-50（含）元',
              1,
              '50-100（含）元',
              2,
              '100-1000（含）元',
              '>1000元') ALLNEWROWE_FEE,
       A.BADNESSINFO_SUM,
       TRIM(TO_CHAR(A.BADNESSINFO_SUM / B.SUM_COUNT * 100, '9990.99')) || '%' BADNESSINFO_ZB
  FROM (SELECT TO_CHAR(T.RECV_TIME, 'mm') YEAR_MONTH,
               (CASE
                 WHEN ALLNEWROWE_FEE BETWEEN 0 AND 50 THEN
                  '0'
                 WHEN ALLNEWROWE_FEE BETWEEN 50 AND 100 THEN
                  '1'
                 WHEN ALLNEWROWE_FEE BETWEEN 100 AND 1000 THEN
                  '2'
                 ELSE
                  '3'
               END) ALLNEWROWE_FEE,
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
                    WHEN ALLNEWROWE_FEE BETWEEN 0 AND 50 THEN
                     '0'
                    WHEN ALLNEWROWE_FEE BETWEEN 50 AND 100 THEN
                     '1'
                    WHEN ALLNEWROWE_FEE BETWEEN 100 AND 1000 THEN
                     '2'
                    ELSE
                     '3'
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
 ORDER BY A.YEAR_MONTH, A.ALLNEWROWE_FEE