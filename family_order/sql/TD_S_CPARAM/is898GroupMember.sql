select count(*) CNT
  from TF_F_CUST_GROUPMEMBER
 where USER_ID = :USER_ID
   and REMOVE_TAG = '0'
   and GROUP_ID like '898%'
   and ROWNUM < 2
