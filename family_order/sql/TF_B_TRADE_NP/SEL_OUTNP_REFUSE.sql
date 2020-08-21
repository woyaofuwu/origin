SELECT A.SERIAL_NUMBER,
       A.BRAND_CODE,
       A.PRODUCT_ID,
       TO_CHAR(A.OPEN_DATE, 'YYYY-MM-DD HH24:MI:SS') OPEN_DATE,
       A.CITY_CODE,
       B.CUST_NAME,
       A.VIP_CLASS_ID,
       A.VIP_TYPE_CODE,
       A.CUST_MANAGER_ID,
       '否' IS_SUCC,
       DECODE(SUBSTR(D.RESULT_INFO, 1, 12),
              'APPEXCEPTION',
              '系统错误',
              D.RESULT_INFO) RESULT_MESSAGE,
       D.CREATE_TIME ACCEPT_DATE,
       Decode(d.Rsrv_Str1, '', '09998980', d.Rsrv_Str1) Port_In_Netid
  FROM (SELECT E.USER_ID,
               E.SERIAL_NUMBER,
               E.BRAND_CODE,
               E.PRODUCT_ID,
               E.OPEN_DATE,
               T.VIP_CLASS_ID,
               T.VIP_TYPE_CODE,
               T.CUST_MANAGER_ID,
               T.VIP_ID,
               E.CUST_ID,
               T.USECUST_ID,
               NVL(c.city_code, e.city_code) city_code
          FROM TF_F_USER E, TF_F_CUST_VIP T, TF_F_USER_CITY c
         where E.CUST_ID = T.CUST_ID(+)
           and t.remove_tag(+) = '0'
           AND e.USER_ID = c.USER_ID(+)
           and c.end_date(+) >= sysdate) A,
       TF_F_CUSTOMER B,
       TL_B_NPTRADE_SOA D
 WHERE A.CUST_ID = B.CUST_ID
   AND A.SERIAL_NUMBER = D.NP_CODE
   AND (A.SERIAL_NUMBER = :SERIAL_NUMBER OR :SERIAL_NUMBER IS NULL)
   AND D.CREATE_TIME >=
       TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'))
   AND (D.CREATE_TIME <
       TRUNC(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')) + 1 OR
       :END_DATE IS NULL)
   AND D.COMMAND_CODE = 'APPLY_REQ'
   AND D.STATE = '011'
 ORDER BY D.CREATE_TIME DESC