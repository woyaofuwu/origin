SELECT A.YEAR_MONTH YEAR_MONTH,
A.DEAL_RAMARK DEAL_RAMARK,
A.BADNESSINFO_SUM BADNESSINFO_SUM,
(TRIM(TO_CHAR(A.BADNESSINFO_SUM / B.SUM_COUNT * 100, '9990.99')) || '%') BADNESSINFO_ZB
FROM (SELECT TO_CHAR(T.RECV_TIME, 'mm') YEAR_MONTH,
       SUM(1) BADNESSINFO_SUM,
       D.DEAL_RAMARK
  FROM TF_F_BADNESS_INFO T, TF_F_BADNESS_INO_DEAL D, TI_B_BADNESS_EXTENDS E
    WHERE 1 = 1
   AND T.INFO_RECV_ID=E.INFO_RECV_ID(+)
   AND (E.BADNESS_INFO_PROVINCE = '898' or t. badness_info_province = '898' )
   AND T.STATE IN ('02', '0A', '0B')
   AND T.INFO_RECV_ID = D.INFO_RECV_ID
   AND D.STATE IN ('02', '0B', '0A')
   and D.deal_date =
       (select max(r.deal_date)
          from TF_F_BADNESS_INO_DEAL r
         where r.info_recv_id = d.info_recv_id)
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
 GROUP BY TO_CHAR(T.RECV_TIME, 'mm'), D.DEAL_RAMARK
 ORDER BY TO_CHAR(T.RECV_TIME, 'mm'), D.DEAL_RAMARK) A
FULL OUTER JOIN (SELECT TO_CHAR(S.RECV_TIME, 'mm') YEAR_MONTH,
                  SUM(1) SUM_COUNT
             FROM TF_F_BADNESS_INFO S, TI_B_BADNESS_EXTENDS EX
            WHERE 1 = 1
              AND S.INFO_RECV_ID=EX.INFO_RECV_ID(+)
              AND (EX.BADNESS_INFO_PROVINCE = '898' or s. badness_info_province = '898' )
              AND S.STATE IN ('02', '0A', '0B')
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