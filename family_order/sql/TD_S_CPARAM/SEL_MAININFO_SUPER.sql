SELECT count(1) recordcount
  FROM tf_f_cust_group
 WHERE super_group_id=:PARAM0
 and remove_tag = '0'