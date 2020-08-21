SELECT TRUNC(MONTHS_BETWEEN(SYSDATE, A.OPEN_DATE)) OPEN_DATE
  FROM TF_F_USER A
 WHERE SERIAL_NUMBER = :SERIAL_NUMBER
   AND REMOVE_TAG = :REMOVE_TAG
   AND NET_TYPE_CODE = :NET_TYPE_CODE