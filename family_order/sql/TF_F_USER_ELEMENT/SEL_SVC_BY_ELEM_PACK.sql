select user_id,product_id,package_id,element_type_code,element_id, start_date,end_date,update_depart_id,update_staff_id,update_time,remark,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3,rsrv_tag1,rsrv_tag2,rsrv_tag3
  from tf_f_user_element 
 where partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   and user_id = :USER_ID
   and element_type_code = 'S'
 --and start_date < sysdate
   and element_id = :ELEMENT_ID
   and product_id = :PRODUCT_ID