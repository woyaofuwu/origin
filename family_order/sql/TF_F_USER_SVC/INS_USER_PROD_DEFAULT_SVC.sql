INSERT INTO tf_f_user_svc(partition_id,user_id,service_id,main_tag,start_date,end_date,update_time)
SELECT MOD(TO_NUMBER(:USER_ID),10000),TO_NUMBER(:USER_ID),service_id,main_tag,TRUNC(SYSDATE),TO_DATE('20501231','yyyymmdd'),sysdate
  FROM td_b_product_svc
 WHERE product_id = :PRODUCT_ID
   AND (main_tag = '1' OR force_tag = '1' OR default_tag = '1')
   AND SYSDATE BETWEEN start_date AND end_date