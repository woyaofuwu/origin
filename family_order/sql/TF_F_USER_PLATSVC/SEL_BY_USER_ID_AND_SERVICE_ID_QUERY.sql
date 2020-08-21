SELECT  A.* FROM  TF_F_USER_PLATSVC A 
WHERE  A.USER_ID=:USER_ID
AND  A.SERVICE_ID=:SERVICE_ID 
AND  A.FIRST_DATE_MON<=TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS')
AND A.FIRST_DATE_MON=(SELECT  MAX(A1.FIRST_DATE_MON) FROM  TF_F_USER_PLATSVC A1 
WHERE  A1.USER_ID=:USER_ID
AND  A1.SERVICE_ID=:SERVICE_ID 
AND  A1.FIRST_DATE_MON<=TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS')
)
ORDER BY A.FIRST_DATE_MON DESC