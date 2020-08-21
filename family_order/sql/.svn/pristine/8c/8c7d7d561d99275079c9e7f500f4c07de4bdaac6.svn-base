Select count(1) recordcount
FROM tf_f_user T 
 where T.user_id = TO_NUMBER(:USER_ID)
 AND T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) 
 AND t.remove_tag='0'
 and t.NET_TYPE_CODE<>'00'