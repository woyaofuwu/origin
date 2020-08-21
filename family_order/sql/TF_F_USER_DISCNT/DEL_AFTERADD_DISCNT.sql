Delete From tf_f_user_discnt c Where  exists( Select 1
from td_b_product_package a,td_b_package_element b
Where  a.product_id= :PRODUCT_ID And a.package_id=b.package_id
And b.element_type_code=:ELEMENT_TYPE_CODE 
 AND SYSDATE Between a.start_date AND a.end_date
 AND SYSDATE BETWEEN b.start_date AND b.end_date
And a.product_id=c.product_id  And a.package_id=c.package_id And b.element_id=c.discnt_code )
And user_id=  :USER_ID
AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
And start_date >= trunc(last_day(SYSDATE)) + 1