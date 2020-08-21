--IS_CACHE=Y
SELECT A.YEAR_MONTH YEAR_MONTH,
       a.vest_operator,
       A.BADNESSINFO_SUM BADNESSINFO_SUM,
       (TRIM(TO_CHAR(A.BADNESSINFO_SUM / B.SUM_COUNT * 100, '9990.99')) || '%') BADNESSINFO_ZB,
       b.SUM_COUNT
  FROM (SELECT TO_CHAR(T.RECV_TIME, 'mm') YEAR_MONTH,
               SUM(1) BADNESSINFO_SUM,
               (case
                 when (SUBSTR(T.BADNESS_INFO, 0, 3) in
                      ('130', '131', '132', '145', '155', '156', '186')) then
                  '电信'
                 when (SUBSTR(T.BADNESS_INFO, 0, 3) in
                      ('010', '022', '0310', '0311', '0312', '0313', '0314', '0315', '0316', '0317', '0318', '0319', '0335', '0349', '0350', '0351', '0352', '133', '153', '189')) then
                  '联通'
                 else
                  '不正确填写'
               end) vest_operator
          FROM TF_F_BADNESS_INFO T
         WHERE 1 = 1
           AND T.TARGET_PROVINCE = '999'
           AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) = :REPORT_TYPE_CODE OR
               :REPORT_TYPE_CODE IS NULL)
           AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR
               :REPORT_CODE IS NULL)
           AND (trunc(T.REPORT_TIME) >=
               TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR
               :REPORT_START_TIME IS NULL)
           AND (trunc(T.REPORT_TIME) <=
               TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR
               :REPORT_END_TIME IS NULL)
         GROUP BY TO_CHAR(T.RECV_TIME, 'mm'),
                  (case
                    when (SUBSTR(T.BADNESS_INFO, 0, 3) in
                         ('130', '131', '132', '145', '155', '156', '186')) then
                     '电信'
                    when (SUBSTR(T.BADNESS_INFO, 0, 3) in
                         ('010', '022', '0310', '0311', '0312', '0313', '0314', '0315', '0316', '0317', '0318', '0319', '0335', '0349', '0350', '0351', '0352', '133', '153', '189')) then
                     '联通'
                    else
                     '不正确填写'
                  end)
         ORDER BY TO_CHAR(T.RECV_TIME, 'mm'),
                  (case
                    when (SUBSTR(T.BADNESS_INFO, 0, 3) in
                         ('130', '131', '132', '145', '155', '156', '186')) then
                     '电信'
                    when (SUBSTR(T.BADNESS_INFO, 0, 3) in
                         ('010', '022', '0310', '0311', '0312', '0313', '0314', '0315', '0316', '0317', '0318', '0319', '0335', '0349', '0350', '0351', '0352', '133', '153', '189')) then
                     '联通'
                    else
                     '不正确填写'
                  end)) A
  FULL OUTER JOIN (SELECT TO_CHAR(S.RECV_TIME, 'mm') YEAR_MONTH,
                          COUNT(S.INFO_RECV_ID) SUM_COUNT
                     FROM TF_F_BADNESS_INFO S
                    WHERE 1 = 1
                      AND S.TARGET_PROVINCE = '999'
                      AND (SUBSTR(S.SORT_RESULT_TYPE, 5, 2) =
                          :REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)
                      AND (SUBSTR(S.SORT_RESULT_TYPE, 7, 2) = '06' OR
                          '06' IS NULL)
                      AND (trunc(S.REPORT_TIME) >=
                          TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR
                          :REPORT_START_TIME IS NULL)
                      AND (trunc(S.REPORT_TIME) <=
                          TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR
                          :REPORT_END_TIME IS NULL)
                    GROUP BY TO_CHAR(S.RECV_TIME, 'mm')
                    ORDER BY TO_CHAR(S.RECV_TIME, 'mm')) B ON  A.YEAR_MONTH =
                                                              B.YEAR_MONTH
 ORDER BY A.YEAR_MONTH