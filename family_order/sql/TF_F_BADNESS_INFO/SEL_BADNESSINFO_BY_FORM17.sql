SELECT A.YEAR_MONTH YEAR_MONTH,
       A.BADNESS_INFO_PROVINCE BADNESS_INFO_PROVINCE,
       A.BADNESSINFO_SUM BADNESSINFO_SUM,
       (TRIM(TO_CHAR(A.BADNESSINFO_SUM / B.SUM_COUNT * 100, '9990.99')) || '%') BADNESSINFO_ZB
  FROM (SELECT TO_CHAR(T.RECV_TIME, 'mm') YEAR_MONTH,
               SUM(1) BADNESSINFO_SUM,
                coalesce(E.badness_info_province,t.badness_info_province) BADNESS_INFO_PROVINCE
          FROM TF_F_BADNESS_INFO T, TI_B_BADNESS_EXTENDS E
         WHERE 1 = 1
           AND T.INFO_RECV_ID=E.INFO_RECV_ID(+)
           AND (E.REPORT_CUST_PROVINCE = '898'  or  t. REPORT_CUST_PROVINCE = '898' )
           AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) = :REPORT_TYPE_CODE OR
               :REPORT_TYPE_CODE IS NULL)
           AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR
               :REPORT_CODE IS NULL)
           AND (trunc(T.REPORT_TIME) >= TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR
               :REPORT_START_TIME IS NULL)
           AND (trunc(T.REPORT_TIME) <= TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR
               :REPORT_END_TIME IS NULL)
         GROUP BY TO_CHAR(T.RECV_TIME, 'mm'),  coalesce(E.badness_info_province,t.badness_info_province)
         ORDER BY TO_CHAR(T.RECV_TIME, 'mm'), BADNESS_INFO_PROVINCE) A
  FULL OUTER JOIN (SELECT TO_CHAR(S.RECV_TIME, 'mm') YEAR_MONTH,
                          SUM(1) SUM_COUNT
                     FROM TF_F_BADNESS_INFO S, TI_B_BADNESS_EXTENDS EX
					            WHERE 1 = 1
					              AND S.INFO_RECV_ID=EX.INFO_RECV_ID(+)
                      AND (EX.REPORT_CUST_PROVINCE = '898'  or s. REPORT_CUST_PROVINCE= '898'  )
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
 ORDER BY A.YEAR_MONTH, A.BADNESS_INFO_PROVINCE