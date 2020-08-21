SELECT to_char(cust_id) cust_id,to_char(obj_cust_id) obj_cust_id,relation_type_code,role_code_a,role_code_b,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,remark 
  FROM tf_f_relation_cc
 WHERE cust_id=TO_NUMBER(:CUST_ID)
   AND obj_cust_id=TO_NUMBER(:OBJ_CUST_ID)
   AND relation_type_code=:RELATION_TYPE_CODE
   and end_date > sysdate