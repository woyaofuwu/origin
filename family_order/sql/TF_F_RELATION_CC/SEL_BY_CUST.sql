SELECT to_char(cc.cust_id) cust_id,cu.cust_name,to_char(obj_cust_id) obj_cust_id,relation_type_code,role_code_a,role_code_b,to_char(start_date,'yyyy-mm-dd') start_date,to_char(end_date,'yyyy-mm-dd') end_date,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,cc.remark
  FROM tf_f_relation_cc cc, tf_f_customer cu
 WHERE
  (cc.cust_id=TO_NUMBER(:CUST_ID) AND cc.obj_cust_id=cu.cust_id)
  OR  (obj_cust_id=TO_NUMBER(:CUST_ID) AND cc.cust_id=cu.cust_id)
  AND to_char(end_date,'yyyy-mm-dd')>to_char(sysdate,'yyyy-mm-dd')