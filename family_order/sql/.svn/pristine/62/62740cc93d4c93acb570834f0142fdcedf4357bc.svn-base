SELECT A.YEAR_MONTH YEAR_MONTH,
       DECODE(:FLAG, '1', DECODE(A.IS_REAL_NAME, '2', '是', '1', '否', '0', '否','不确定'),
			  '0', DECODE(A.IS_REAL_NAME, '2', '是', '1', '是', '0', '否', '不确定')) IS_REAL_NAME,
       DECODE(:FLAG, '1',DECODE(A.IS_REAL_NAME,'2',A.IS_REAL_SUM,'1',A.NOT_REAL_SUM, '0', A.NOT_REAL_SUM, A.NOT_SURE_SUM),
              '0',DECODE(A.IS_REAL_NAME,'2',A.IS_REAL_SUM,'1',A.IS_REAL_SUM, '0', A.NOT_REAL_SUM, A.NOT_SURE_SUM)
             ) BADNESSINFO_SUM,
       DECODE(A.IS_REAL_NAME,
              '1',
              TRIM(TO_CHAR(A.IS_REAL_SUM / B.SUM_COUNT * 100, '9990.99')) || '%',
              '0',
              TRIM(TO_CHAR(A.NOT_REAL_SUM / B.SUM_COUNT * 100, '9990.99')) || '%',
              TRIM(TO_CHAR(A.NOT_SURE_SUM / B.SUM_COUNT * 100, '9990.99')) || '%') BADNESSINFO_ZB
  FROM (SELECT TO_CHAR(T.RECV_TIME, 'mm') YEAR_MONTH,
               T.IS_REAL_NAME IS_REAL_NAME,
               SUM(DECODE(:FLAG, '1', DECODE(T.IS_REAL_NAME, '2', 1, 0), '0', DECODE(T.IS_REAL_NAME, '2', 1, '1', 1, 0))) IS_REAL_SUM,
               SUM(DECODE(:FLAG, '1', DECODE(T.IS_REAL_NAME, '0', 1 ,'1', 1, 0))) NOT_REAL_SUM,
               SUM(DECODE(T.IS_REAL_NAME, NULL, 1, 0)) NOT_SURE_SUM
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
         GROUP BY TO_CHAR(T.RECV_TIME, 'mm'), T.IS_REAL_NAME
         ORDER BY TO_CHAR(T.RECV_TIME, 'mm'), T.IS_REAL_NAME) A
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
                    ORDER BY TO_CHAR(S.RECV_TIME, 'mm')) B ON A.YEAR_MONTH =
                                                                   B.YEAR_MONTH
order by a.YEAR_MONTH,a.IS_REAL_NAME