SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,discnt_code,spec_tag,relation_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_discnt a
 WHERE partition_id=:PARTITION_ID
   AND user_id=TO_NUMBER(:USER_ID)
   AND EXISTS(SELECT 1 FROM td_b_product_discnt WHERE product_id=:PRODUCT_ID 
                AND discnt_code=a.discnt_code AND force_tag = '1')
   AND end_date>sysdate