select b.VIP_CLASS_ID,
       b.VIP_TYPE_CODE,
       TO_CHAR(A.OPEN_DATE, 'YYYY-MM-DD HH24:MI:SS') OPEN_DATE,
       NVL(c.city_code, a.city_code) city_code
  from TF_F_USER a, TF_F_CUST_VIP b, TF_F_USER_CITY c
 WHERE A.USER_ID = :USER_ID
   AND c.USER_ID(+) = :USER_ID
   and c.end_date(+) >= sysdate
   AND A.REMOVE_TAG = '0'
   AND A.CUST_ID = B.CUST_ID(+)
   AND B.REMOVE_TAG(+) = '0'
   AND (NVL(c.city_code, a.city_code) = :AREA_CODE OR 'HAIN' = :AREA_CODE)