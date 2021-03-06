SELECT PARTITION_ID,TO_CHAR(USER_ID) USER_ID,PRODUCT_ID,BRAND_CODE,SERIAL_NUMBER,IMSI,TO_CHAR(START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE 
  FROM TF_F_USER_INFOCHANGE
 WHERE PARTITION_ID=MOD(TO_NUMBER(:USER_ID),10000)
   AND USER_ID=TO_NUMBER(:USER_ID)
   AND TO_DATE(:TIME_POINT, 'YYYYMMDDHH24MISS') >= START_DATE
   AND TO_DATE(:TIME_POINT, 'YYYYMMDDHH24MISS') <= END_DATE
   AND END_DATE > START_DATE