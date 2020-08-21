SELECT A.REPORT_TYPE_CODE,
       DECODE(A.REPORT_TYPE_CODE,
              '01',
              '网络安全',
              '02',
              '资料安全',
              '03',
              '垃圾邮件',
              '04',
              '垃圾短信',
              '05',
              '垃圾彩信',
              '06',
              '骚扰电话',
              '07',
              'WAP涉黄信息',
              A.REPORT_TYPE_CODE) REPORT_TYPE_NAME,
       A.REPORT_CODE,
       DECODE(A.REPORT_CODE,
              '01',
              '省内网内点对点',
              '02',
              '省际网内点对点',
              '03',
              'SP',
              '04',
              '行业应用',
              '05',
              '自有业务',
              '06',
              '其他运营商',
              '07',
              '其他',
              A.REPORT_CODE) REPORT_NAME,
       SUM(A.UNDO_SUM) UNDO_SUM,
       SUM(A.DONE_SUM) DONE_SUM,
       (TRIM(TO_CHAR(SUM(A.DONE_SUM) / (SUM(A.UNDO_SUM) + SUM(A.DONE_SUM)) * 100,
                     '9990.99')) || '%') DONE_LV
  FROM (SELECT NVL(SUBSTR(T.SORT_RESULT_TYPE, 5, 2), '07') REPORT_TYPE_CODE,
               NVL(SUBSTR(T.SORT_RESULT_TYPE, 7, 2), '07') REPORT_CODE,
               DECODE(T.STATE, '00', 1, '01', 1, '04', 1, 0) UNDO_SUM,
               DECODE(T.STATE, '02', 1, '03', 1, '0A', 1, '0B', 1, 0) DONE_SUM
          FROM TF_F_BADNESS_INFO T, TI_B_BADNESS_EXTENDS E
         WHERE 1 = 1
           AND T.INFO_RECV_ID=E.INFO_RECV_ID(+)
           AND (E.BADNESS_INFO_PROVINCE = '898' or t. badness_info_province = '898' )
           AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) = :REPORT_TYPE_CODE OR
               :REPORT_TYPE_CODE IS NULL)
           AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR
               :REPORT_CODE IS NULL)
           AND (trunc(T.REPORT_TIME) >= TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR
               :REPORT_START_TIME IS NULL)
           AND (trunc(T.REPORT_TIME) <= TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR
               :REPORT_END_TIME IS NULL)) A
 GROUP BY A.REPORT_TYPE_CODE, A.REPORT_CODE
 ORDER BY A.REPORT_TYPE_CODE, A.REPORT_CODE