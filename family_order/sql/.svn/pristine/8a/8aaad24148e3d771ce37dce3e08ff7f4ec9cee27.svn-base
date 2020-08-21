select count(1) CNT
  from TF_F_USER C,
       (select B.USER_ID_B, B.SERIAL_NUMBER_B
          from TF_F_RELATION_UU A, TF_F_RELATION_UU B
         where A.USER_ID_A = B.USER_ID_A
           and A.RELATION_TYPE_CODE = '56'
           and A.USER_ID_B = :USER_ID
           and A.ROLE_CODE_B = '2'
           and B.ROLE_CODE_B = '1'
           and A.END_DATE > sysdate
           and B.END_DATE > sysdate) D
 where C.USER_ID = D.USER_ID_B
   and C.OPEN_DATE < sysdate - 365
   and C.REMOVE_TAG = '0'
