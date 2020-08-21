select count(1) CNT
  from TF_F_RELATION_UU A
 where USER_ID_B = :USER_ID_B
   and sysdate between START_DATE and END_DATE
   and exists
 (select 1
          from TF_F_USER
         where SERIAL_NUMBER in ('V0HN001010','V0HK004960')
           and REMOVE_TAG = '0'
           and USER_ID = A.USER_ID_A)