SELECT COUNT(1) recordcount
FROM   tf_f_user_sale_active a, td_b_package p
WHERE  user_id = :USER_ID
AND    (product_id = :PRODUCT_ID OR :PRODUCT_ID = '99')
and    a.package_id = p.package_id
and    p.package_kind_code = :PACKAGE_KIND_CODE
AND    a.process_tag = :PROCESS_TAG
AND    a.end_date > trunc(last_day(SYSDATE)) + 1
and    a.partition_id = mod(to_number(:USER_ID), 10000)