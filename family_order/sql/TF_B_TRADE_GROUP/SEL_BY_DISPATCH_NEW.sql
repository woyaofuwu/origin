SELECT to_char(trade_id) trade_id,trade_type_code,group_id,group_cust_card_no,mp_group_cust_code,unify_pay_code,class_id,group_attr,client_status,user_count,company_address,post_code,website,fax_nbr,email,group_contact_phone,enterprise_scope,province,city,calling_type_code,calling_sub_type_code,trade_class1,trade_class2,calling_area_code,enterprise_type_code,enterprise_size_code,to_char(juristic_cust_id) juristic_cust_id,juristic,juristic_type_code,all_emp_count,china_emp_count,local_emp_count,group_memo,emp_lsave,to_char(reg_money) reg_money,cust_aim,scope,to_char(finance_earning) finance_earning,to_char(comm_budget) comm_budget,main_trade,mob_count,unicom_133,unicom_130,mobile_num_xlt,consume,to_char(turnover) turnover,to_char(year_gain) year_gain,super_group_id,payfor_way_code,manager_staff_id,doyen_staff_id,newtrade_comment,to_char(employee_arpu) employee_arpu,writefee_count,to_char(writefee_sum) writefee_sum,to_char(boss_fee_sum) boss_fee_sum,to_char(latency_fee_sum) latency_fee_sum,vpmn_id,area_code,scp_code,vpmn_type,sub_state,func_tlags,inter_feeindex,out_feeindex,outgrp_feeindex,subgrp_feeindex,pre_ip_no,pre_ip_disc,othor_ip_disc,trans_no,max_close_num,max_num_close,person_maxclose,max_outnum,max_users,to_char(pkg_start_date,'yyyy-mm-dd hh24:mi:ss') pkg_start_date,pkg_type,discount,to_char(limit_fee) limit_fee,zone_max,zonefree_num,to_char(zone_fee) zone_fee,mt_maxnum,aip_id,to_char(accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,trade_staff_id,trade_depart_id,trade_city_code,trade_eparchy_code,client_info1,client_info2,client_info3,client_info4,client_info5,client_info6,client_info7,client_tag1,client_tag2,remark 
  FROM (select a.*,rownum tmpnum from tf_b_trade_group a
 WHERE (:GROUP_ID is null  or a.group_id like  '%'||:GROUP_ID||'%')
  AND (:STARTDATE  is null  or a.accept_date >=to_date(:STARTDATE,'yyyy-mm-dd'))
  AND (:ENDDATE is null or a.accept_date<=to_date(:ENDDATE,'yyyy-mm-dd'))
  AND (:CLIENT_STATUS is null or a.client_status =:CLIENT_STATUS)
  AND (:CALLING_TYPE_CODE is null or a.calling_type_code =:CALLING_TYPE_CODE)
  AND (:CALLING_SUB_TYPE_CODE is null or a.calling_sub_type_code =:CALLING_SUB_TYPE_CODE)
  AND (:MANAGER_STAFF_ID is NULL  or a.manager_staff_id=:MANAGER_STAFF_ID)
  AND (a.trade_type_code='5005')
  AND EXISTS
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