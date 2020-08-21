SELECT COUNT(*) recordcount
From tf_f_user_sale_active a, td_b_package_element b    
Where a.user_id = TO_NUMBER(:USER_ID)
And a.partition_id = Mod(TO_NUMBER(:USER_ID), 10000)
And a.PROCESS_TAG = '0'
AND a.end_date >SYSDATE
AND b.package_id = a.package_id
AND b.element_type_code ='A'