SELECT cust_id,cust_name,group_id,calling_type_code,calling_sub_type_code,super_group_id 
  FROM tf_f_cust_group 
 WHERE (:CALLING_TYPE_CODE IS NULL OR calling_type_code=:CALLING_TYPE_CODE) 
   AND (:CALLING_SUB_TYPE_CODE IS NULL OR calling_sub_type_code=:CALLING_SUB_TYPE_CODE)
   and remove_tag = '0'