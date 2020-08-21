UPDATE tf_f_cust_group 
   SET  manager_staff_id = :MANAGER_STAFF_ID
 WHERE group_id = :GROUP_ID AND remove_tag ='0'