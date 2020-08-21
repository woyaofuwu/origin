select count(*) CNT
  from TF_SM_TROOP_MEMBER   T,
       TF_F_RELATION_UU     G
 where T.CUST_CODE = G.SERIAL_NUMBER_A
   and G.USER_ID_B = :USER_ID
   and G.RELATION_TYPE_CODE = '20'
   and G.END_DATE > sysdate
   and MEMBER_STATUS = '1'
   and T.TROOP_ID = NVL(:RSRV_STR4, 0)