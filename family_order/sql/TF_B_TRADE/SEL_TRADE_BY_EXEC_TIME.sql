SELECT A.*
  FROM TF_B_TRADE A
 WHERE A.USER_ID = :USER_ID
   AND A.EXEC_TIME = TO_DATE(:EXEC_TIME,'yyyy-mm-dd hh24:mi:ss')
   AND A.SUBSCRIBE_TYPE >= 200
   AND A.SUBSCRIBE_TYPE < 300