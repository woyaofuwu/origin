--IS_CACHE=Y
SELECT brand_code, brand
  FROM td_s_brand t
 where 1 = 1
   and t.end_date > sysdate
 order by brand, brand_code