UPDATE TF_F_USER_ATTR
   SET END_DATE = SYSDATE-0.00001,UPDATE_TIME=SYSDATE
 WHERE USER_ID = TO_NUMBER(:USER_ID)
   AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000)
   AND END_DATE > SYSDATE