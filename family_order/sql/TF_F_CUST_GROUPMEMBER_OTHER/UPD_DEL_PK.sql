UPDATE tf_f_cust_groupmember_other
   SET destroy_date=sysdate,remove_tag='1'  
 WHERE cust_id=TO_NUMBER(:CUST_ID)
   AND member_cust_id=TO_NUMBER(:MEMBER_CUST_ID)
   and remove_tag = '0'