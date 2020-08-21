SELECT COUNT(1) recordcount
FROM   tf_f_user_sale_active a
WHERE  user_id = :USER_ID
And    (product_id = :PRODUCT_ID OR :PRODUCT_ID = '99')
AND    process_tag = :PROCESS_TAG
AND    end_date > trunc(last_day(SYSDATE)) + 1
AND partition_id = mod(to_number(:USER_ID),10000)