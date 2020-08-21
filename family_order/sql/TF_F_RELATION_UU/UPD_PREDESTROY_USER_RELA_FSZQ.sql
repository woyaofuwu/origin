UPDATE tf_f_relation_uu
SET end_date = (CASE  WHEN RELATION_TYPE_CODE in(select distinct RELATION_TYPE_CODE from td_b_product_comp)  THEN SYSDATE
                      WHEN (RELATION_TYPE_CODE ='70')  THEN to_date(:EXP_DATE,'YYYY-MM-DD hh24:mi:ss')
                      ELSE to_date(:EXP_DATE,'YYYY-MM-DD hh24:mi:ss') END),update_time=SYSDATE
 WHERE user_id_b = :USER_ID_B
   AND partition_id = MOD(TO_NUMBER(:USER_ID_B),10000) 
   AND relation_type_code != '30' 
   AND end_date > SYSDATE