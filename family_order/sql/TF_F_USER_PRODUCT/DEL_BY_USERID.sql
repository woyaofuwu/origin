DELETE FROM tf_f_user_product a
WHERE a.user_id=:USER_ID
AND   a.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
AND   a.product_id=:PRODUCT_ID
AND   a.start_date=:START_DATE