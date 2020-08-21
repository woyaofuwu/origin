SELECT TO_CHAR(a.USER_ID) USER_ID,
       a.SERIAL_NUMBER,
       NVL(c.city_code, a.city_code) city_code
  FROM TF_F_USER a, TF_F_USER_CITY c
 WHERE a.USER_ID = TO_NUMBER(:USER_ID)
 AND a.partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND a.USER_ID = c.USER_ID(+)
   and c.end_date(+) >= sysdate