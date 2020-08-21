update Tf_f_User_Sale_Active t
set t.end_date=to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
 where t.USER_ID = :USER_ID
   and t.PRODUCT_ID = :PRODUCT_ID
   and t.PACKAGE_ID = :PACKAGE_ID