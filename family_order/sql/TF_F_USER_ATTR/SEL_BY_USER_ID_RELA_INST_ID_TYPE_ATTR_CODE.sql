SELECT T.*
  FROM TF_F_USER_ATTR T
 WHERE T.USER_ID = :USER_ID
   AND T.INST_TYPE = :INST_TYPE
   AND T.RELA_INST_ID = :RELA_INST_ID
   AND T.ATTR_CODE LIKE :ATTR_CODE
   AND T.END_DATE > SYSDATE