SELECT COUNT(1) recordcount
FROM tf_f_cust_vip
WHERE user_id = :USER_ID
  AND (vip_class_id = :CLASS_ID OR :CLASS_ID = '*')
  AND remove_tag='0'