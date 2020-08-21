SELECT A.*
  FROM TF_F_RELATION_XXT A,TF_F_USER_DISCNT B
 WHERE 1=1
      AND A.EC_USER_ID = :EC_USER_ID 
			AND A.USER_ID_A = :USER_ID_A 
			 AND A.SERVICE_ID = :SERVICE_ID 
			 AND A.RELA_INST_ID=B.INST_ID
      AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE
			AND SYSDATE BETWEEN B.START_DATE AND B.END_DATE