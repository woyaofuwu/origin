--IS_CACHE=N
SELECT A.USER_ID,
       A.SERVICE_ID,
       A.START_DATE,
       A.END_DATE,
       A.BIZ_STATE_CODE
  FROM TF_F_USER_PLATSVC A
 WHERE 1 = 1
   AND A.BIZ_STATE_CODE IN ('A', 'N')
   AND USER_ID = :USER_ID
   AND PARTITION_ID = MOD(:USER_ID, 10000)
   AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE