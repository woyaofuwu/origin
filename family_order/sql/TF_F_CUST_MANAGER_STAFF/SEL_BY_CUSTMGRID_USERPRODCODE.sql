select f.cust_manager_id, f.serial_number link_phone, f.area_code, r.start_date, r.end_date, r.update_staff_id, r.update_depart_id, r.user_product_code, r.right_code, r.remark, r.update_time 
  from tf_f_cust_manager_staff f,tf_m_staff_grp_right r 
    where f.cust_manager_id = r.staff_id 
      and f.job_type = '2' 
      and r.staff_id = :CUST_MANAGER_ID 
      and r.user_product_code = :USER_PRODUCT_CODE 
      and sysdate between r.start_date and r.end_date 