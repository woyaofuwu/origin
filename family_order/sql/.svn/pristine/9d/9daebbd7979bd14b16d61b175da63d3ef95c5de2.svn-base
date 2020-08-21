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
              '其他对象') REPORT_TYPE_NAME,
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
              '其他分类',
              '其他分类') REPORT_NAME,
       SUM(A.LOCAL_SUM) LOCAL_SUM,
       SUM(A.REMOTE_SUM) REMOTE_SUM
  FROM (SELECT NVL(SUBSTR(T.SORT_RESULT_TYPE, 5, 2), '07') REPORT_TYPE_CODE,
               NVL(SUBSTR(T.SORT_RESULT_TYPE, 7, 2), '07') REPORT_CODE,
               DECODE( coalesce(E.badness_info_province,t.badness_info_province) , '898', 1, 0) LOCAL_SUM,
               DECODE( coalesce(E.badness_info_province,t.badness_info_province) , '898', 0, 1) REMOTE_SUM
          FROM TF_F_BADNESS_INFO T , TI_B_BADNESS_EXTENDS E
         WHERE 1 = 1
           AND T.INFO_RECV_ID=E.INFO_RECV_ID(+)
           AND (E.REPORT_CUST_PROVINCE= '898'  or t. REPORT_CUST_PROVINCE = '898'  )
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