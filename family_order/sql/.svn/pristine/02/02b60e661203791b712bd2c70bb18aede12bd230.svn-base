SELECT /*+index(a)*/ *
  From tf_f_user_discnt a 
  Where user_id_a In(
     Select user_id_a From tf_f_user_discnt b 
     Where b.user_id=to_number(:USER_ID)
     And user_id_a In(Select user_id From tf_f_user c 
           Where c.user_id = b.user_id_a And c.product_id=:PRODUCT_ID))
     And a.end_date+0>Sysdate