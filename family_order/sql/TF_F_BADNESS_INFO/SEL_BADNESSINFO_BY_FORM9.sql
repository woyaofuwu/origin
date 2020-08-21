--IS_CACHE=Y
SELECT A.YEAR_MONTH,
       DECODE(A.BRAND_CODE,
              '01',
              '全球通',
              '02',
              '动感地带',
              '03',
              '神州行',
              '不正确填写') BADNESSINFO_BRAND,
       DECODE(A.BRAND_CODE,
              '01',
              G001_SUM,
              '02',
              G010_SUM,
              '03',
              G002_SUM,
              NULL_SUM) BADNESSINFO_SUM,
       DECODE(A.BRAND_CODE,
              '01',
              (TRIM(TO_CHAR(A.G001_SUM / B.SUM_COUNT * 100, '9990.99')) || '%'),
              '02',
              (TRIM(TO_CHAR(A.G010_SUM / B.SUM_COUNT * 100, '9990.99')) || '%'),
              '03',
              (TRIM(TO_CHAR(A.G002_SUM / B.SUM_COUNT * 100, '9990.99')) || '%'),
              (TRIM(TO_CHAR(A.NULL_SUM / B.SUM_COUNT * 100, '9990.99')) || '%')) BADNESSINFO_ZB
  FROM ((SELECT TO_CHAR(T.RECV_TIME, 'mm') YEAR_MONTH,
                T.BRAND_CODE BRAND_CODE,
                SUM(DECODE(T.BRAND_CODE, '01', 1, 0)) G001_SUM,
                SUM(DECODE(T.BRAND_CODE, '02', 1, 0)) G010_SUM,
                SUM(DECODE(T.BRAND_CODE, '03', 1, 0)) G002_SUM,
                SUM(DECODE(T.BRAND_CODE, NULL, 1, 0)) NULL_SUM
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
          GROUP BY TO_CHAR(T.RECV_TIME, 'mm'), T.BRAND_CODE
          ORDER BY TO_CHAR(T.RECV_TIME, 'mm'), T.BRAND_CODE) A FULL
        OUTER JOIN
        (SELECT TO_CHAR(S.RECV_TIME, 'mm') YEAR_MONTH, SUM(1) SUM_COUNT
           FROM TF_F_BADNESS_INFO S
          WHERE 1 = 1
           AND (SUBSTR(S.SORT_RESULT_TYPE, 5, 2) = :REPORT_TYPE_CODE OR
               :REPORT_TYPE_CODE IS NULL)
           AND (SUBSTR(S.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR
               :REPORT_CODE IS NULL)
           AND (trunc(S.REPORT_TIME) >= TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR
               :REPORT_START_TIME IS NULL)
           AND (trunc(S.REPORT_TIME) <= TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR
               :REPORT_END_TIME IS NULL)
          GROUP BY TO_CHAR(S.RECV_TIME, 'mm')
          ORDER BY TO_CHAR(S.RECV_TIME, 'mm')) B ON
        A.YEAR_MONTH = B.YEAR_MONTH)
order by a.YEAR_MONTH,a.BRAND_CODE