select count(1) CNT
  from TF_F_RELATION_UU A
 where A.RELATION_TYPE_CODE = :RELATION_TYPE_CODE
   and A.END_DATE > TRUNC(LAST_DAY(sysdate)) + 1 - 1 / 24 / 60 / 60
   and exists
 (select 1
          from TF_F_RELATION_UU B
         where B.USER_ID_A = A.USER_ID_A
           and B.USER_ID_B = :USER_ID
           and B.RELATION_TYPE_CODE = :RELATION_TYPE_CODE
           and B.END_DATE > TRUNC(LAST_DAY(sysdate)) + 1 - 1 / 24 / 60 / 60)
           