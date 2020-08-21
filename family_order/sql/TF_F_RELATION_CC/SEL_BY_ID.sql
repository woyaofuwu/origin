SELECT to_char(a.cust_id) cust_id,to_char(obj_cust_id) obj_cust_id,a.relation_type_code,role_code_a,role_code_b,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time,a.update_staff_id,a.update_depart_id,a.remark,b.cust_name,c.relation_type_name
  FROM tf_f_relation_cc a,tf_f_customer b,td_s_relation c
 WHERE a.cust_id = TO_NUMBER(:CUST_ID)
   AND a.obj_cust_id = b.cust_id 
   AND c.relation_type_code=a.relation_type_code