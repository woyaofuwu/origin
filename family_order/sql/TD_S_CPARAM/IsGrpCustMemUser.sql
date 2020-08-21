SELECT COUNT(1) recordcount
FROM tf_f_user a WHERE user_id=:USER_ID_A
AND EXISTS (SELECT 1 FROM tf_f_cust_groupmember WHERE a.cust_id=cust_id AND remove_tag='0'
AND user_id=:USER_ID)