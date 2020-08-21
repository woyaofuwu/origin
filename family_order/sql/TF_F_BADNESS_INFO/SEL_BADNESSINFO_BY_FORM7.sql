SELECT A.YEAR_MONTH YEAR_MONTH,
       A.PROVINCE_TYPE PROVINCE_TYPE,
       DECODE(PROVINCE_TYPE,
              '省内举报数量',
              A.LOCAL_REPORT_SUM,
              A.REMOTE_REPORT_SUM) BADNESSINFO_SUM,
       DECODE(PROVINCE_TYPE,
              '省内举报数量',
              TRIM(TO_CHAR(A.LOCAL_REPORT_SUM / B.SUM_COUNT * 100, '9990.99')) || '%',
              TRIM(TO_CHAR(A.REMOTE_REPORT_SUM / B.SUM_COUNT * 100,
                           '9990.99')) || '%') BADNESSINFO_ZB
  FROM (SELECT TO_CHAR(T.RECV_TIME, 'mm') YEAR_MONTH,
               SUM(DECODE( coalesce(E.badness_info_province,t.badness_info_province) ,
                          coalesce(E.report_cust_province,t.report_cust_province),
                          1,
                          0)) LOCAL_REPORT_SUM,
               SUM(DECODE( coalesce(E.badness_info_province,t.badness_info_province) ,
                          coalesce(E.report_cust_province,t.report_cust_province),
                          0,
                          1)) REMOTE_REPORT_SUM,
               DECODE( coalesce(E.badness_info_province,t.badness_info_province) ,
                      coalesce(E.report_cust_province,t.report_cust_province),
                      '省内举报数量',
                      '省外举报数量') PROVINCE_TYPE
          FROM TF_F_BADNESS_INFO T, TI_B_BADNESS_EXTENDS E
         WHERE 1 = 1
           AND T.INFO_RECV_ID=E.INFO_RECV_ID(+)
           AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) = :REPORT_TYPE_CODE OR
               :REPORT_TYPE_CODE IS NULL)
           AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR
               :REPORT_CODE IS NULL)
           AND (trunc(T.REPORT_TIME) >= TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR
               :REPORT_START_TIME IS NULL)
           AND (trunc(T.REPORT_TIME) <= TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR
               :REPORT_END_TIME IS NULL)
         GROUP BY TO_CHAR(T.RECV_TIME, 'mm'),
                  DECODE( coalesce(E.badness_info_province,T.badness_info_province) ,
                         coalesce(E.report_cust_province,T.report_cust_province),
                         '省内举报数量',
                         '省外举报数量')
         ORDER BY TO_CHAR(T.RECV_TIME, 'mm'),
                  DECODE( coalesce(E.badness_info_province,T.badness_info_province) ,
                         coalesce(E.report_cust_province,T.report_cust_province),
                         '省内举报数量',
                         '省外举报数量')) A
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
 ORDER BY A.YEAR_MONTH, A.PROVINCE_TYPE