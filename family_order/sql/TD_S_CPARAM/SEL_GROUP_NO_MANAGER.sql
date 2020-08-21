SELECT count(*) recordcount FROM tf_f_cust_group a
 WHERE NOT EXISTS(SELECT 1 FROM td_m_staff WHERE staff_id=a.manager_staff_id)
   AND remove_tag='0'