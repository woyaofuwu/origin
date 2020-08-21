--IS_CACHE=N
SELECT COUNT(1) 业务数
  FROM TF_F_RELATION_UU B
 WHERE B.RELATION_TYPE_CODE = '47'
   AND B.SERIAL_NUMBER_B = 'KD_' || :MOBILE_NUM
   AND B.END_DATE > SYSDATE
   AND EXISTS (SELECT 1
          FROM TF_F_USER_SALE_ACTIVE UA
         WHERE UA.SERIAL_NUMBER = :MOBILE_NUM
           AND UA.PROCESS_TAG = '0'
           AND UA.PRODUCT_ID IN (67220429)
           AND UA.END_DATE > SYSDATE)
   AND ROWNUM < 2