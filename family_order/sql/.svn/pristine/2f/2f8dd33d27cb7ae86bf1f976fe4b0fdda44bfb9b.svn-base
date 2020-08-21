SELECT b.serial_number, a.DEVELOP_DEPART_ID, a.In_Date, a.open_date, a.Brand_Code, a.product_id,a.DEVELOP_STAFF_ID
FROM (SELECT a1.serial_number, a1.DEVELOP_DEPART_ID, a1.In_Date, a1.open_date, a1.DEVELOP_STAFF_ID ,a2.brand_code,a2.product_id
FROM tf_f_user a1,tf_f_user_product a2 WHERE a1.user_id = a2.user_id and a1.remove_tag = '0' and a2.main_tag = '1' and sysdate between a2.start_date and a2.end_date ) a, tf_f_baduser_query_import b
WHERE a.serial_number(+) = b.serial_number
AND b.batch_id = :BATCH_ID