select count(*) CNT
  from TF_SM_TROOP_MEMBER
 where USER_ID = :USER_ID
   AND MEMBER_STATUS = '1'
   and INSTR(:PRODUCT_STR3, TROOP_ID) > 0