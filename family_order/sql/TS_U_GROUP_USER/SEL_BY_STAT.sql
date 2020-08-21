SELECT stat_month brand,cust_manager_id,to_char(cust_id) cust_id,group_id,to_char(user_id) user_id,product_id,brand_code,vpmn_id,to_char(fee_sum) fee_sum,class_id,subclass_id,member_cnt,remove_tag 
  FROM ts_u_group_user
 WHERE stat_month=:BRAND
   AND group_id=:GROUP_ID