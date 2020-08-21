--IS_CACHE=Y
SELECT A.YEAR_MONTH YEAR_MONTH,
       decode(A.IN_MODE_CODE,'01','本地','全网') IN_MODE,
       A.BADNESSINFO_SUM BADNESSINFO_SUM,
       (TRIM(TO_CHAR(A.BADNESSINFO_SUM / B.SUM_COUNT * 100, '9990.99')) || '%') BADNESSINFO_ZB
  FROM (SELECT TO_CHAR(T.RECV_TIME, 'mm') YEAR_MONTH,
               SUM(1) BADNESSINFO_SUM,
               T.IN_MODE_CODE
          FROM TF_F_BADNESS_INFO T
         WHERE 1 = 1
           AND t.in_mode_code IS NOT NULL
           AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) = :REPORT_TYPE_CODE OR
               :REPORT_TYPE_CODE IS NULL)
           AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR
               :REPORT_CODE IS NULL)
           AND (trunc(T.REPORT_TIME) >= TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR
               :REPORT_START_TIME IS NULL)
           AND (trunc(T.REPORT_TIME) <= TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR
               :REPORT_END_TIME IS NULL)
         GROUP BY TO_CHAR(T.RECV_TIME, 'mm'), T.IN_MODE_CODE
         ORDER BY TO_CHAR(T.RECV_TIME, 'mm'), T.IN_MODE_CODE) A
  FULL OUTER JOIN (SELECT TO_CHAR(S.RECV_TIME, 'mm') YEAR_MONTH,
                          COUNT(S.INFO_RECV_ID) SUM_COUNT
                     FROM TF_F_BADNESS_INFO S
                    WHERE 1 = 1
                      /*AND s.in_mode_code IS NOT NULL*/
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
                    GROUP BY TO_CHAR(S.RECV_TIME, 'mm')) B ON A.YEAR_MONTH = B.YEAR_MONTH
order by A.YEAR_MONTH,A.IN_MODE_CODE