SELECT to_char(a.cust_id) cust_id,a.cust_name cust_name,group_id,b.acct_id prevaluec1,b.pay_name prevaluec2,null group_cust_card_no,mp_group_cust_code,unify_pay_code,class_id,group_attr,GROUP_STATUS client_status,USER_NUM user_count,GROUP_ADDR company_address,post_code,website,fax_nbr,email,group_contact_phone,enterprise_scope,null province,null city,calling_type_code,SUB_CALLING_TYPE_CODE calling_sub_type_code,null trade_class1,null trade_class2,calling_area_code,enterprise_type_code,enterprise_size_code,to_char(juristic_cust_id) juristic_cust_id,JURISTIC_NAME juristic,
juristic_type_code,EMP_NUM_ALL all_emp_count,EMP_NUM_CHINA china_emp_count,EMP_NUM_LOCAL local_emp_count,group_memo,emp_lsave,to_char(reg_money) reg_money,cust_aim,scope,to_char(finance_earning) finance_earning,
to_char(comm_budget) comm_budget,main_trade,MOBILE_NUM_GLOBAL mob_count,UNICOM_NUM_C unicom_133,UNICOM_NUM_G unicom_130,TELECOM_NUM_XLT mobile_num_xlt,consume,
to_char(turnover) turnover,to_char(year_gain) year_gain,super_group_id,payfor_way_code,
CUST_MANAGER_ID manager_staff_id,doyen_staff_id,
newtrade_comment,to_char(employee_arpu) employee_arpu,writefee_count,to_char(writefee_sum) writefee_sum,
to_char(boss_fee_sum) boss_fee_sum,to_char(latency_fee_sum) latency_fee_sum,remark,subclass_id,
a.remove_tag remove_tag,to_char(PRODUCT_NUM_USE) product_num,to_char(VPMN_NUM) vpmn_count,null prevaluec3,
null prevaluec4,null prevalue1,
null prevalue2,null prevalue3,null prevalue4,
null prevalue5,null prevaluen1,null prevaluen2,null prevaluen3,null prevalued1,null prevalued2,null rsrv_1,null rsrv_2 
  FROM tf_f_cust_group a,tf_f_account b
 WHERE group_id=:GROUP_ID
 AND a.cust_id=b.cust_id
 AND a.remove_tag='0'