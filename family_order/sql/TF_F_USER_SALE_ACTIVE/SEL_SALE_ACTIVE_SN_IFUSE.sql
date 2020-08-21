select t.User_Id,T.SERIAL_NUMBER,T.PRODUCT_ID,T.PACKAGE_ID,T.START_DATE,T.END_DATE,T.RSRV_TAG2
  from tf_f_user_sale_active t
 where t.product_id =:PRODUCT_ID
   and t.USER_ID = :USER_ID
   and t.process_tag = '0' 
   and sysdate < t.end_date
   and t.start_date < t.end_date