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
       D.RESULT_MESSAGE,
       D.ACCEPT_DATE,
       D.PORT_IN_NETID
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
       TF_B_TRADE_NP D
 WHERE A.USER_ID = D.USER_ID
   AND A.CUST_ID = B.CUST_ID
   AND D.TRADE_TYPE_CODE = '41'
   AND D.RSRV_STR5 LIKE '%FAIL|%'
   AND (A.SERIAL_NUMBER = :SERIAL_NUMBER OR :SERIAL_NUMBER IS NULL)
   AND D.ACCEPT_DATE >=
       TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'))
   AND (D.ACCEPT_DATE <
       TRUNC(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')) + 1 OR
       :END_DATE IS NULL)
 ORDER BY ACCEPT_DATE DESC