SELECT to_char(cust_id) cust_id,cust_name,group_id,group_cust_card_no,mp_group_cust_code,unify_pay_code,class_id,group_attr,client_status,user_count,company_address,post_code,website,fax_nbr,email,group_contact_phone,enterprise_scope,province,city,calling_type_code,calling_sub_type_code,trade_class1,trade_class2,calling_area_code,enterprise_type_code,enterprise_size_code,to_char(juristic_cust_id) juristic_cust_id,juristic,juristic_type_code,all_emp_count,china_emp_count,local_emp_count,group_memo,emp_lsave,to_char(reg_money) reg_money,cust_aim,scope,to_char(finance_earning) finance_earning,to_char(comm_budget) comm_budget,main_trade,mob_count,unicom_133,unicom_130,mobile_num_xlt,consume,to_char(turnover) turnover,to_char(year_gain) year_gain,super_group_id,payfor_way_code,manager_staff_id,doyen_staff_id,newtrade_comment,to_char(employee_arpu) employee_arpu,writefee_count,to_char(writefee_sum) writefee_sum,to_char(boss_fee_sum) boss_fee_sum,to_char(latency_fee_sum) latency_fee_sum,remark 
  FROM tf_f_cust_group
 WHERE remove_tag = '0'
   and ( cust_name like '%'||:CUST_NAME||'%' or :CUST_NAME is null )
   AND ( class_id=:CLASS_ID or :CLASS_ID is null )
   AND ( company_address=:COMPANY_ADDRESS or :COMPANY_ADDRESS is null )
   AND ( enterprise_scope=:ENTERPRISE_SCOPE or :ENTERPRISE_SCOPE is null )
   AND ( calling_type_code=:CALLING_TYPE_CODE or :CALLING_TYPE_CODE is null )
   AND ( calling_sub_type_code=:CALLING_SUB_TYPE_CODE or :CALLING_SUB_TYPE_CODE is null )
   AND ( enterprise_type_code=:ENTERPRISE_TYPE_CODE or :ENTERPRISE_TYPE_CODE is null )
   AND ( enterprise_size_code=:ENTERPRISE_SIZE_CODE or :ENTERPRISE_SIZE_CODE is null )
   AND ( prevaluen1>=:MOB_COUNT or :MOB_COUNT is null )
   AND ( super_group_id=:SUPER_GROUP_ID or :SUPER_GROUP_ID is null )
   AND ( manager_staff_id=:MANAGER_STAFF_ID or :MANAGER_STAFF_ID is null )