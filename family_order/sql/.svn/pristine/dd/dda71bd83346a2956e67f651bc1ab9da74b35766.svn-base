SELECT COUNT(1) recordcount
FROM   tf_f_user_sale_active
WHERE  user_id = :USER_ID
And    (product_id = :PRODUCT_ID OR :PRODUCT_ID = '99')
AND    process_tag = :PROCESS_TAG
AND    end_date > to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss')
and    partition_id = mod(to_number(:USER_ID), 10000)
and    rownum < 2