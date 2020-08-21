--IS_CACHE=Y
SELECT A.YEAR_MONTH YEAR_MONTH,
       A.BADNESSINFO_SUM BADNESSINFO_SUM,
       A.CONTENT_TYPE_CODE CONTENT_TYPE_CODE,
       (TRIM(TO_CHAR(A.BADNESSINFO_SUM / B.SUM_COUNT * 100, '9990.99')) || '%') BADNESSINFO_ZB
  FROM (SELECT TO_CHAR(T.RECV_TIME, 'mm') YEAR_MONTH,
               SUM(1) BADNESSINFO_SUM,
               DECODE(SUBSTR(T.SORT_RESULT_TYPE, 5, 2),
                      '04',
                      '01',
                      '05',
                      '03',
                      '06',
                      '02',
                      '04') || nvl(SUBSTR(T.SORT_RESULT_TYPE, 9, 2),'09') CONTENT_TYPE_CODE
          FROM TF_F_BADNESS_INFO T
         WHERE 1 = 1
           AND T.STATE in ('02','04','0A','0B')
           AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) = :REPORT_TYPE_CODE OR
               :REPORT_TYPE_CODE IS NULL)
           AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR
               :REPORT_CODE IS NULL)
           AND (trunc(T.REPORT_TIME) >= TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR
               :REPORT_START_TIME IS NULL)
           AND (trunc(T.REPORT_TIME) <= TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR
               :REPORT_END_TIME IS NULL)
         GROUP BY TO_CHAR(T.RECV_TIME, 'mm'),
                  DECODE(SUBSTR(T.SORT_RESULT_TYPE, 5, 2),
                         '04',
                         '01',
                         '05',
                         '03',
                         '06',
                         '02',
                         '04') || nvl(SUBSTR(T.SORT_RESULT_TYPE, 9, 2),'09')) A
  FULL OUTER JOIN (SELECT TO_CHAR(S.RECV_TIME, 'mm') YEAR_MONTH,
                          SUM(1) SUM_COUNT
                     FROM TF_F_BADNESS_INFO S
                    WHERE 1 = 1
                      AND S.STATE in ('02','04','0A','0B')
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
                    ORDER BY TO_CHAR(S.RECV_TIME, 'mm')) B ON A.YEAR_MONTH =
                                                                   B.YEAR_MONTH
 ORDER BY A.YEAR_MONTH, A.CONTENT_TYPE_CODE