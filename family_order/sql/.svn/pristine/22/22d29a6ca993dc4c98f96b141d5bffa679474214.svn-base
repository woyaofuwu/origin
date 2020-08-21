SELECT T.INFO_RECV_ID,
       T.RECV_IN_TYPE,
       T.REPORT_SERIAL_NUMBER,
       T.REPORT_CUST_NAME,
       DECODE(SUBSTR(T.SORT_RESULT_TYPE, 5, 2),
              '04',
              '01',
              '05',
              '03',
              '06',
              '02',
              '04') || NVL(SUBSTR(T.SORT_RESULT_TYPE, 9, 2), '09') CONTENT_TYPE_CODE,
       coalesce(E.report_cust_province,t.report_cust_province) report_cust_province,
       T.REPORT_BRAND_CODE,
       E.SERV_REQUEST_TYPE,
       T.RECV_PROVINCE,
       TO_CHAR(T.REPORT_TIME, 'yyyy-mm-dd hh24:mi:ss') REPORT_TIME,
       T.BADNESS_INFO,
        coalesce(E.badness_info_province,t.badness_info_province) badness_info_province,
       T.TARGET_PROVINCE,
       E.REPORT_TYPE_CODE,
       T.RECV_CONTENT,
       T.IMPORTANT_LEVEL,
       T.STATE,
       DECODE(T.STATE,
              '00',
              '待回复',
              '01',
              '待处理',
              '02',
              '已回复',
              '03',
              '已退回',
              '04',
              '已催办',
              '0A',
              '已归档',
              '0B',
              '已短信通知用户',
              STATE) STATE_NAME,
       DECODE(NVL(SUBSTR(T.SORT_RESULT_TYPE, 7, 2), '07'),
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
              NVL(SUBSTR(T.SORT_RESULT_TYPE, 7, 2), '07')) REPORT_NAME,
       DECODE(NVL(SUBSTR(T.SORT_RESULT_TYPE, 5, 2), '07'),
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
              NVL(SUBSTR(T.SORT_RESULT_TYPE, 5, 2), '07')) REPORT_TYPE_NAME,
       T.HASTEN_STATE,
       T.REPEAT_REPORT,
       DECODE(T.REPEAT_REPORT, '0', '否', '1', '是', REPEAT_REPORT) REPEAT_REPORT_NAME,
       T.STICK_LIST,
       T.RECORD_FILE_LIST,
       T.DEAL_RAMARK,
       T.DEAL_REMARK_MAKEUP,
       DECODE(T.BRAND_CODE, 'G001', '01', 'G002', '03', 'G010', '02', t.brand_code)BRAND_CODE,
       T.ALLNEWROWE_FEE,
       T.IN_DATE,
       T.IN_MODE_CODE
  FROM TF_F_BADNESS_INFO T,TI_B_BADNESS_EXTENDS E
 WHERE 1 = 1
   AND T.INFO_RECV_ID=E.INFO_RECV_ID(+)
   AND T.SORT_RESULT_TYPE= '010207'
   AND (T.DEAL_RAMARK = :DEAL_REMARK OR :DEAL_REMARK IS NULL)
   AND (T.RECV_IN_TYPE = :RECV_IN_TYPE OR :RECV_IN_TYPE IS NULL)
   AND (T.IN_MODE_CODE = :IN_MODE_CODE OR
       :IN_MODE_CODE IS NULL)
   AND (E.BADNESS_INFO_PROVINCE = :BADNESS_INFO_PROVINCE OR
       :BADNESS_INFO_PROVINCE IS NULL)
   AND (T.BADNESS_INFO = :BADNESS_INFO OR
       :BADNESS_INFO IS NULL)
   AND (T.REPORT_SERIAL_NUMBER = :REPORT_SERIAL_NUMBER OR
       :REPORT_SERIAL_NUMBER IS NULL)
   AND (trunc(T.REPORT_TIME) >= TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR
       :REPORT_START_TIME IS NULL)
   AND (trunc(T.REPORT_TIME) <= TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR
       :REPORT_END_TIME IS NULL)
   AND (T.STATE = :STATE OR :STATE IS NULL)