SELECT A.PARTITION_ID,
       A.USER_ID,
       A.SERVICE_ID,
       
       A.BIZ_STATE_CODE,
       to_char(A.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,      
       to_char(A.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE
  FROM TF_F_USER_PLATSVC A
 WHERE A.PARTITION_ID = MOD(:USER_ID, 10000)
   AND A.USER_ID = :USER_ID
   AND A.END_DATE > SYSDATE
   
   
