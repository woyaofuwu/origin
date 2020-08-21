SELECT A.*
  FROM TD_B_PLATSVC A
 WHERE A.SERVICE_ID = :SERVICE_ID
   AND A.BIZ_STATE_CODE = 'A'
   AND A.START_DATE < SYSDATE
   AND A.END_DATE > SYSDATE
   AND EXISTS (SELECT 1
          FROM TD_M_SP_BIZ B
         WHERE B.SP_CODE = A.SP_CODE
           AND B.BIZ_CODE = A.BIZ_CODE
           AND B.BIZ_STATE_CODE = 'A'
           AND B.BIZ_TYPE_CODE = :BIZ_TYPE_CODE
           AND B.START_DATE < SYSDATE
           AND B.END_DATE > SYSDATE)