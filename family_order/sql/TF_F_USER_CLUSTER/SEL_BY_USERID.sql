select a.GROUP_ID,a.USER_ID,a.ROLE_CODE,b.TYPE_ID,b.LEADER_USER_ID,b.ACCT_ID as COMP_ACCT_ID,
 b.CUST_ID as COMP_CUST_ID 
 from TF_F_USER_CLUSTER_RELA a, TF_F_USER_CLUSTER b
 where a.GROUP_ID=b.GROUP_ID
 and sysdate between a.START_DATE and a.END_DATE
 and sysdate between b.START_DATE and b.END_DATE
 and a.USER_ID=:USER_ID