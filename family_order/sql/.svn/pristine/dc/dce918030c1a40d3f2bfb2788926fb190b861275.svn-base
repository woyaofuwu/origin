select a.element_id,
       a.element_type_code,
       a.product_id,
       a.package_id,
       a.force_tag,
       a.default_tag
  from tf_f_user_grp_package a
 where 1 = 1
   and a.user_id = :USER_ID
   and a.partition_id = mod(TO_NUMBER(:USER_ID), 10000)
   and a.package_id = :PACKAGE_ID
   and a.element_type_code IN ('S','D')
   and sysdate between a.start_date and a.end_date