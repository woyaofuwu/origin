select count(1) recordcount from tf_f_user where 
1=1
and PREPAY_TAG <> '0'
and user_id = TO_NUMBER(:USER_ID)
and PARTITION_ID=MOD(TO_NUMBER(:USER_ID), 10000)
and remove_tag = '0'