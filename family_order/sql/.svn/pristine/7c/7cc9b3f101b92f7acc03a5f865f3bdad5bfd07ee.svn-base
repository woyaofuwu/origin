select '' SUBSYS_CODE, 0 PARAM_ATTR, '' PARAM_CODE, '' PARAM_NAME, b.serial_number_b para_code1, ( select vpn_name from tf_f_user_vpn where vpn_no = :PARA_CODE1 ) para_code2, ( select cust_name from tf_f_customer where cust_id = a.usecust_id ) para_code3, ( select class_name from td_m_vipclass where vip_type_code = a.vip_type_code and class_id = a.class_id ) para_code4, ( select vip_manager_name from tf_f_managerstaff where VIP_MANAGER_ID = a.cust_manager_id ) para_code5, ( select remark from tf_f_customer where cust_id = a.usecust_id ) para_code6, a.class_id para_code7, '' para_code8, '' PARA_CODE9 , '' PARA_CODE10 , '' PARA_CODE11 , '' PARA_CODE12 , '' PARA_CODE13 , '' PARA_CODE14 , '' PARA_CODE15 , '' PARA_CODE16 , '' PARA_CODE17 , '' PARA_CODE18 , '' PARA_CODE19 , '' PARA_CODE20 , '' PARA_CODE21 , '' PARA_CODE22 , '' PARA_CODE23 , '' PARA_CODE24 , '' PARA_CODE25 , '' PARA_CODE26 , '' PARA_CODE27 , '' PARA_CODE28 , '' PARA_CODE29 , '' PARA_CODE30 , '' START_DATE , '' END_DATE , '' EPARCHY_CODE , '' REMARK , '' UPDATE_STAFF_ID , '' UPDATE_DEPART_ID , '' UPDATE_TIME
    from tf_f_cust_vip a, tf_f_relation_uu b,tf_f_user c
    where c.serial_number = :PARA_CODE1
        and c.remove_tag = '0'
        and b.user_id_a = c.user_id
        and b.relation_type_code = :PARA_CODE2
        AND sysdate > b.start_date and sysdate < b.end_date
        and a.user_id = b.user_id_b
        and ( a.vip_type_code = :PARA_CODE3 OR ( :PARA_CODE3 IS NULL AND ( a.vip_type_code = '0' or a.vip_type_code = '2' ) ) )
        and ( a.class_id = :PARA_CODE4 OR :PARA_CODE4 IS NULL ) and
      (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL) and
      (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL) and
      (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL) and
      (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL) and
      (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL) and
      (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)