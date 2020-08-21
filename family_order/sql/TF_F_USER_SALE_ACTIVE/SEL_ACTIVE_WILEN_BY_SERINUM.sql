select t.* 
FROM TF_F_USER_SALE_ACTIVE T
WHERE T.PRODUCT_ID IN ('69908001', '69908015', '69908012')
and T.SERIAL_NUMBER = :SERIAL_NUMBER 
and sysdate < t.end_date
and t.start_date<t.end_date 