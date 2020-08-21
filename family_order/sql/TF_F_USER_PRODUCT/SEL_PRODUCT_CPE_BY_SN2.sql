select to_char(t1.open_date,'yyyymmdd') OPEN_DATE,t.*
  from tf_f_user_product t, tf_f_user t1
 where t.user_id = t1.user_id
   and t1.serial_number=:SERIAL_NUMBER
   and t.brand_code = 'CPE1'
   AND T.PRODUCT_ID = '10001001'