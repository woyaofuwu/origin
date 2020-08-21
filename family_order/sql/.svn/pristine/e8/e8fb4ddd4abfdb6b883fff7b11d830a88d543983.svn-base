SELECT COUNT(1) recordcount
FROM  tf_f_user_svc a
WHERE user_id=to_number(:USER_ID)
  AND partition_id=MOD(to_number(:USER_ID),10000)
  AND sysdate BETWEEN start_date AND end_date+0
  AND service_id+0 BETWEEN 13 AND 19
  AND NOT EXISTS (SELECT 1 FROM td_b_product_package b,td_b_package_element c
                            WHERE sysdate BETWEEN b.start_date AND b.end_date
                              AND (b.eparchy_code=:EPARCHY_CODE OR b.eparchy_code='ZZZZ')
                              AND b.product_id=:PRODUCT_ID
                              AND b.package_id=c.package_id
                              AND c.element_type_code='S'
                              AND sysdate BETWEEN c.start_date AND c.end_date
                              AND c.element_id=a.service_id)