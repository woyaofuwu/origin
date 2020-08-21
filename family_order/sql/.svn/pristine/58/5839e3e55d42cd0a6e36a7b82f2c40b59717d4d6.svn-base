SELECT COUNT(1) recordcount FROM tf_f_user
WHERE user_id=:USER_ID and partition_id=mod(:USER_ID,10000)
and (product_id=:PRODUCT_ID or :PRODUCT_ID=-1)
and (
Select Count(1) From tf_f_relation_uu a Where
  user_id_a=:USER_ID and end_date>sysdate
  ) > :NUM