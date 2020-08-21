SELECT '' SERIAL_NUMBER_A,
       TO_CHAR(A.SERIAL_NUMBER) SERIAL_NUMBER_B,
       TO_CHAR(U.IMS_PASSWORD) IMS_PASSWORD
  FROM TF_F_USER_IMPU U, TF_F_USER A
 WHERE 1 = 1
   AND U.PARTITION_ID = MOD(TO_NUMBER(:USER_ID_B), 10000)
   AND U.USER_ID = TO_NUMBER(:USER_ID_B)
   AND U.PARTITION_ID = A.PARTITION_ID
   AND U.USER_ID = A.USER_ID
   AND A.REMOVE_TAG = '0'
   AND U.RSRV_STR1 IN ('0', '1', '2')
   AND SYSDATE BETWEEN U.START_DATE AND U.END_DATE