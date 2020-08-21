SELECT MAX(sms_score) SMS_SCORE,MAX(vip_score) VIP_SCORE,MAX(vip_score_base) VIP_SCORE_BASE
FROM
(
SELECT score sms_score,NULL vip_score,NULL vip_score_base
FROM tf_f_user_newscore
WHERE user_id=TO_NUMBER(:USER_ID) AND partition_id=MOD(TO_NUMBER(:USER_ID),10000) AND id_type='1'
AND acyc_id=(SELECT MAX(acyc_id)
FROM tf_f_user_newscore
WHERE user_id=TO_NUMBER(:USER_ID) AND partition_id=MOD(TO_NUMBER(:USER_ID),10000) AND id_type='1')
UNION ALL
SELECT NULL ,score,NULL
FROM tf_f_user_newscore
WHERE user_id=TO_NUMBER(:USER_ID) AND partition_id=MOD(TO_NUMBER(:USER_ID),10000) AND id_type='2'
AND acyc_id=(SELECT MAX(acyc_id)
FROM tf_f_user_newscore
WHERE user_id=TO_NUMBER(:USER_ID) AND partition_id=MOD(TO_NUMBER(:USER_ID),10000) AND id_type='2')
UNION ALL
SELECT NULL ,NULL,score
FROM tf_f_user_newscore
WHERE user_id=TO_NUMBER(:USER_ID) AND partition_id=MOD(TO_NUMBER(:USER_ID),10000) AND id_type='0' AND score_type_code='04'
AND acyc_id=(SELECT MAX(acyc_id)
FROM tf_f_user_newscore
WHERE user_id=TO_NUMBER(:USER_ID) AND partition_id=MOD(TO_NUMBER(:USER_ID),10000) AND id_type='0' AND score_type_code='04')
)