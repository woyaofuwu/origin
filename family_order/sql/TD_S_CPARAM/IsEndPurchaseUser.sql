SELECT COUNT(1) recordcount
FROM   tf_f_user_sale_active a, td_b_package p
WHERE  a.user_id = :USER_ID
and    a.partition_id = mod(to_number(:USER_ID), 10000)
And    (a.product_id = :PRODUCT_ID OR :PRODUCT_ID = '99')
AND    a.package_id = p.package_id
and    p.package_kind_code like :PACKAGE_KIND_CODE
AND    a.process_tag = :PROCESS_TAG
AND    a.end_date < SYSDATE