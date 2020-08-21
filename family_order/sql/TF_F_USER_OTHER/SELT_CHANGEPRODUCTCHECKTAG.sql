SELECT 1 TOP,count(1) X_CHOICE_TAG
FROM tf_f_user_foregift
WHERE user_id=:USER_ID
AND foregift_code='8'
AND money>0
UNION ALL
SELECT 2,count(1)
FROM tf_f_user_svc
WHERE user_id=:USER_ID AND service_id=31
AND partition_id=MOD(:USER_ID,10000)
AND end_date+0>SYSDATE
UNION ALL
SELECT 3,count(1)
FROM tf_f_user_svc
WHERE user_id=:USER_ID AND service_id=84
AND partition_id=MOD(:USER_ID,10000)
AND end_date+0>SYSDATE
UNION ALL
SELECT 4,count(1)
FROM tf_f_user_svc
WHERE user_id=:USER_ID AND service_id=15
AND partition_id=MOD(:USER_ID,10000)
AND end_date+0>SYSDATE
UNION ALL
SELECT 5,count(1)
FROM tf_f_user_svc
WHERE user_id=:USER_ID AND service_id=18
AND partition_id=MOD(:USER_ID,10000)
AND end_date+0>SYSDATE
UNION ALL
SELECT 6,count(1)
FROM tf_f_user_svc
WHERE user_id=:USER_ID AND service_id=19
AND partition_id=MOD(:USER_ID,10000)
AND end_date+0>SYSDATE
UNION ALL
SELECT 7,count(1)
FROM tf_f_postinfo
WHERE ID =:USER_ID AND id_type='1' AND post_tag='1'
AND end_date+0>SYSDATE