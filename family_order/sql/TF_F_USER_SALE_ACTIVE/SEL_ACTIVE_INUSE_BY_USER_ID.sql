SELECT T.*
  FROM TF_F_USER_SALE_ACTIVE T
 WHERE T.PRODUCT_ID = :PRODUCT_ID
   AND T.PROCESS_TAG = '0'
   AND T.USER_ID = :USER_ID
   AND T.START_DATE < T.END_DATE
   AND T.END_DATE > TRUNC(LAST_DAY(SYSDATE) + 1)