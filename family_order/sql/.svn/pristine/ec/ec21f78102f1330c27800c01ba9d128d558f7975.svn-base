SELECT t.package_id, t.element_type_code,  
        t.element_id element_id, t.default_tag,t.force_tag,  
        to_char(t.start_date, 'yyyy-mm-dd') start_date,  to_char(t.end_date, 'yyyy-mm-dd') end_date,  
       to_char(t.update_time, 'yyyy-mm-dd') update_time,t.update_staff_id, t.update_depart_id,  
        t.remark ,t.product_id  
 FROM   tf_f_user_grp_package t
 WHERE  1=1 
        and t.element_type_code = 'D' 
        and t.user_id = :USER_ID  
        and partition_id=mod(TO_NUMBER(:USER_ID),10000) 
        AND SYSDATE BETWEEN t.START_DATE AND t.END_DATE  
        order by t.element_type_code desc 