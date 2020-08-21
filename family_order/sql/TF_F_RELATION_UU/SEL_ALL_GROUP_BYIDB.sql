Select /*+index(a)*/* From tf_f_relation_uu a 
  Where user_id_a In(
     Select user_id_a From tf_f_relation_uu b 
     Where b.user_id_b=:USER_ID
     And b.role_code_b='1'
     And user_id_a In(Select user_id From tf_f_user c Where b.user_id_a=c.user_id And c.product_id=to_number(:PRODUCT_ID)))
     And a.end_date+0>Sysdate