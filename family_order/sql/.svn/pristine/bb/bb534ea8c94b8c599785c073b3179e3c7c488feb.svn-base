SELECT count(*) recordcount
FROM tf_f_user WHERE user_id=(SELECT MAX(user_id) FROM tf_f_user WHERE serial_number=:SERIAL_NUMBER AND eparchy_code=:EPARCHY_CODE)