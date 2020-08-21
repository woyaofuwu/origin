SELECT t.user_id,t.serial_number,pm.product_id_b 
from tf_f_user t,td_b_product_meb pm 
where t.product_id=:PRODUCT_ID 
and t.user_id=to_number(:USER_ID)
and t.PARTITION_ID=mod(to_number(:USER_ID),10000) 
and t.product_id=pm.product_id and t.remove_tag='0'