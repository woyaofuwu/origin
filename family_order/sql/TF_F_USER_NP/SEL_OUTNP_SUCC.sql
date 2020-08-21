SELECT A.SERIAL_NUMBER,
       A.BRAND_CODE,
       A.PRODUCT_ID,
       TO_CHAR(A.OPEN_DATE, 'YYYY-MM-DD HH24:MI:SS') OPEN_DATE,
       A.CITY_CODE,
       B.CUST_NAME,
       A.VIP_TYPE_CODE,
       A.VIP_CLASS_ID,
       A.CUST_MANAGER_ID,
       '是' IS_SUCC,
       D.NP_TAG,
       D.PORT_IN_NETID,
       D.APPLY_DATE ACCEPT_DATE
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
           and c.end_date(+) >= sysdate) A ,TF_F_CUSTOMER B,
       TF_F_USER_NP D
 WHERE A.USER_ID = D.USER_ID
   AND A.CUST_ID = B.CUST_ID
   AND D.NP_TAG IN ('3', '4', '5', '8')
   AND (A.SERIAL_NUMBER = :SERIAL_NUMBER OR :SERIAL_NUMBER IS NULL)
   AND D.APPLY_DATE >=
       TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'))
   AND (D.APPLY_DATE <
       TRUNC(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')) + 1 OR
       :END_DATE IS NULL)
 order by D.APPLY_DATE desc