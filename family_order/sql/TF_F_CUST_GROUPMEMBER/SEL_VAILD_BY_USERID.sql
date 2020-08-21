select TO_CHAR(USER_ID) USER_ID,
       TO_CHAR(CUST_ID) CUST_ID,
       GROUP_ID,
       GROUP_CUST_NAME  from tf_f_cust_groupmember t where t.remove_tag='0' 
   AND USER_ID = TO_NUMBER(:USER_ID)
   AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)