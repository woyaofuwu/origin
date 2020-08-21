SELECT count(*) recordcount FROM tf_f_cust_groupmember a
 WHERE NOT EXISTS (SELECT 1 FROM td_m_staff WHERE staff_id=a.cust_manager_id)
   AND remove_tag='0'