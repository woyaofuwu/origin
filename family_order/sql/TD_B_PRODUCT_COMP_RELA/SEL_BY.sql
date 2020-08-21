--IS_CACHE=Y
SELECT product_id_a,product_id_b,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,remark 
  FROM td_b_product_comp_rela
 WHERE product_id_a=:PRODUCT_ID_A