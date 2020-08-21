SELECT product_id
FROM tf_f_user_infochange
WHERE user_id=:user_id AND partition_id=MOD(:user_id,10000)
AND start_date<end_date+0
AND SYSDATE<end_date+0
AND start_date=(SELECT MAX(start_date)
				FROM tf_f_user_infochange
				WHERE user_id=:user_id AND partition_id=MOD(:user_id,10000)
				AND start_date<end_date+0
				AND SYSDATE<end_date+0)