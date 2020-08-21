SELECT D.INST_ID
  FROM TF_F_USER_DISCNT D
 WHERE D.USER_ID = :USER_ID
   AND D.DISCNT_CODE = :DISCNT_CODE
   AND D.END_DATE > SYSDATE
 ORDER BY D.END_DATE DESC