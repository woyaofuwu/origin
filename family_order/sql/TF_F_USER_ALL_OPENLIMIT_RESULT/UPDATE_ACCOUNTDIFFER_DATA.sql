UPDATE TF_F_USER_ALL_OPENLIMIT_RESULT A 
 SET A.SYN_RESULT = :SYN_RESULT
WHERE 1=1
 AND A.PHONE_NUMBER = :PHONE_NUMBER
 AND A.COMP_RESULT = :COMP_RESULT