UPDATE tf_f_relation_cc
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),update_time=TO_DATE(:UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS'),update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,remark=:REMARK  
 WHERE cust_id=TO_NUMBER(:CUST_ID)
   AND obj_cust_id=TO_NUMBER(:OBJ_CUST_ID) AND relation_type_code='32'