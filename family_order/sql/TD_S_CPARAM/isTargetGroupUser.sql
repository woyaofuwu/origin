select count(*) CNT
  from TF_SM_TROOP_MEMBER    T,
       TF_F_CUST_GROUPMEMBER G
 where T.CUST_CODE = G.GROUP_ID
   and G.USER_ID = :USER_ID
   and G.REMOVE_TAG = '0'
   and MEMBER_STATUS = '1'
   and T.TROOP_ID = NVL(:RSRV_STR3, 0)