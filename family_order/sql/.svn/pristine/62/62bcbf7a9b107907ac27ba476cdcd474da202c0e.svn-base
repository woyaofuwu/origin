SELECT to_char(cust_id) cust_id,cust_name,group_id,group_cust_card_no,mp_group_cust_code,unify_pay_code,class_id,group_attr,client_status,user_count,company_address,post_code,website,fax_nbr,email,group_contact_phone,enterprise_scope,province,city,calling_type_code,calling_sub_type_code,trade_class1,trade_class2,calling_area_code,enterprise_type_code,enterprise_size_code,to_char(juristic_cust_id) juristic_cust_id,juristic,juristic_type_code,all_emp_count,china_emp_count,local_emp_count,group_memo,emp_lsave,to_char(reg_money) reg_money,cust_aim,scope,to_char(finance_earning) finance_earning,to_char(comm_budget) comm_budget,main_trade,mob_count,unicom_133,unicom_130,mobile_num_xlt,consume,to_char(turnover) turnover,to_char(year_gain) year_gain,super_group_id,payfor_way_code,manager_staff_id,doyen_staff_id,newtrade_comment,to_char(employee_arpu) employee_arpu,writefee_count,to_char(writefee_sum) writefee_sum,to_char(boss_fee_sum) boss_fee_sum,to_char(latency_fee_sum) latency_fee_sum,remark,subclass_id,remove_tag,to_char(product_num) product_num,to_char(vpmn_count) vpmn_count,prevaluec1,prevaluec2,prevaluec3,prevaluec4,prevalue1,prevalue2,prevalue3,prevalue4,prevalue5,to_char(prevaluen1) prevaluen1,to_char(prevaluen2) prevaluen2,to_char(prevaluen3) prevaluen3,to_char(prevalued1,'yyyy-mm-dd') prevalued1,to_char(prevalued2,'yyyy-mm-dd') prevalued2,rsrv_1,rsrv_2 
FROM (SELECT a.*,rownum tmpnum FROM tf_f_cust_group a,
		(
		  SELECT group_id,COUNT(1) FROM tf_f_group_order 
		  where (finish_date > sysdate or finish_date is null)
		  GROUP BY group_id
		  HAVING COUNT(*)>= :PRODUCT_NUM ) c
WHERE a.group_id = c.group_id 
   AND  a.remove_tag = '0'
   and (cust_name like '%'||:CUST_NAME||'%' OR :CUST_NAME IS NULL)
   AND (class_id=:CLASS_ID OR :CLASS_ID IS NULL)
   AND (client_status=:CLIENT_STATUS OR :CLIENT_STATUS IS NULL)
   AND (company_address like '%'||:COMPANY_ADDRESS||'%' OR :COMPANY_ADDRESS IS NULL)
   AND (enterprise_scope=:ENTERPRISE_SCOPE OR :ENTERPRISE_SCOPE IS NULL)
   AND (calling_type_code=:CALLING_TYPE_CODE OR :CALLING_TYPE_CODE IS NULL)
   AND (calling_sub_type_code=:CALLING_SUB_TYPE_CODE 
         OR :CALLING_SUB_TYPE_CODE IS NULL)
   AND (enterprise_type_code=:ENTERPRISE_TYPE_CODE 
          OR :ENTERPRISE_TYPE_CODE IS NULL)
   AND (enterprise_size_code=:ENTERPRISE_SIZE_CODE 
         OR :ENTERPRISE_SIZE_CODE IS NULL)
   AND (prevaluen1>=:MOB_COUNT OR :MOB_COUNT IS NULL)
   AND (super_group_id=:SUPER_GROUP_ID OR :SUPER_GROUP_ID IS NULL)
   AND (manager_staff_id=:MANAGER_STAFF_ID OR :MANAGER_STAFF_ID IS NULL)
   and (subclass_id=:SUBCLASS_ID or :SUBCLASS_ID IS NULL)
   and (group_attr=:GROUP_ATTR or :GROUP_ATTR IS NULL)
   AND not exists
     ( select 1
       from tf_f_user b,tf_f_user_vpmn c
       WHERE b.user_id = c.user_id 
       and a.cust_id = b.cust_id 
       and b.remove_tag = '0')
   AND exists
     ( SELECT 1 
       FROM tf_f_group_order b
       WHERE a.group_id = b.group_id
       AND ( b.prevaluec1 in  
               ( 
	         select product_name from td_b_grp_product
    	         start with product_name = :PRODUCT_ID 
                 connect by prior product_name = brand_code 
		)    OR :PRODUCT_ID IS NULL)
       and ( b.finish_date > sysdate or b.finish_date is null)
     )
  and exists
      ( select 1
        from td_m_staff e,td_m_depart f
        where a.manager_staff_id = e.staff_id
        and e.depart_id = f.depart_id
        and f.depart_frame like (
           select t.depart_frame from td_m_depart t where t.depart_id= :DEPART_ID)||'%' 
         and (f.parent_depart_id = :PARENT_DEPART_ID OR :PARENT_DEPART_ID IS NULL ) ) 
) a
WHERE tmpnum >= :X_REMARK1 
AND (:X_REMARK2 is null OR tmpnum<=:X_REMARK2)