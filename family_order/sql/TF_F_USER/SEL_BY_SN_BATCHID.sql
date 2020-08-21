SELECT b.serial_number, a.DEVELOP_DEPART_ID, a.In_Date, a.open_date, a.Brand_Code, a.product_id,a.DEVELOP_STAFF_ID
FROM (SELECT serial_number, DEVELOP_DEPART_ID, In_Date, open_date, Brand_Code, product_id, DEVELOP_STAFF_ID FROM tf_f_user WHERE remove_tag = '0' ) a, tf_f_baduser_query_import b
WHERE a.serial_number(+) = b.serial_number
AND b.batch_id = :BATCH_ID