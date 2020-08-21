--IS_CACHE=Y
SELECT A.YEAR_MONTH,
       A.BADNESSINFO_SUM,
       A.STATE_NAME,
       (TRIM(TO_CHAR(A.BADNESSINFO_SUM / B.SUM_COUNT * 100, '9990.99')) || '%') BADNESSINFO_ZB
  FROM (SELECT TO_CHAR(T.RECV_TIME, 'mm') YEAR_MONTH,
               SUM(1) BADNESSINFO_SUM,
               DECODE(T.STATE,
                      '00',
                      '未处理',
                      '01',
                      '未处理',
                      '04',
                      '未处理',
                      '已处理') STATE_NAME
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
                  DECODE(T.STATE,
                         '00',
                         '未处理',
                         '01',
                         '未处理',
                         '04',
                         '未处理',
                         '已处理')
         ORDER BY TO_CHAR(T.RECV_TIME, 'mm'),
                  DECODE(T.STATE,
                         '00',
                         '未处理',
                         '01',
                         '未处理',
                         '04',
                         '未处理',
                         '已处理')) A
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
                    GROUP BY TO_CHAR(S.RECV_TIME, 'mm')
                    ORDER BY TO_CHAR(S.RECV_TIME, 'mm')) B ON  A.YEAR_MONTH =
                                                                   B.YEAR_MONTH
 ORDER BY A.YEAR_MONTH, A.STATE_NAME