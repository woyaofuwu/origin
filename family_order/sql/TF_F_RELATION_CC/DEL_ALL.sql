DELETE FROM tf_f_relation_cc
 WHERE cust_id=TO_NUMBER(:CUST_ID) AND obj_cust_id=TO_NUMBER(:OBJ_CUST_ID)