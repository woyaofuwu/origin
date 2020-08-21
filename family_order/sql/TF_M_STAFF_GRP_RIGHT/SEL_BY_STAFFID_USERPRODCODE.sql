
select r.staff_id,r.start_date,r.end_date,r.update_staff_id,r.update_depart_id,r.user_product_code,r.right_code,r.remark,r.update_time 
    from tf_m_staff_grp_right r 
        where 1 = 1 
		and r.staff_id = :CUST_MANAGER_ID 
		and r.user_product_code = :USER_PRODUCT_CODE 
		and sysdate between r.start_date and r.end_date 