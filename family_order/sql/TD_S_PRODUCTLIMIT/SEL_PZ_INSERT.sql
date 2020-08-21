--IS_CACHE=Y
INSERT INTO td_s_productlimit(product_id_a,product_id_b,limit_tag,start_date,end_date,update_staff_id,update_depart_id,update_time)
 VALUES(:PRODUCT_ID_A,:PRODUCT_ID_B,:LIMIT_TAG,TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),:UPDATE_STAFF_ID,:UPDATE_DEPART_ID,TO_DATE(:UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS'))