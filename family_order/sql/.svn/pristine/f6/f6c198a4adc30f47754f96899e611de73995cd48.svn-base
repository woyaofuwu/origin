UPDATE tf_f_cust_group
   SET  REMOVE_TAG = '1',prevalued1=sysdate,prevaluec2=:PREVALUEC2,prevaluec3=:PREVALUEC3  
 WHERE cust_id=TO_NUMBER(:CUST_ID)
   AND group_id=:GROUP_ID
   AND remove_tag = '0'