SELECT COUNT(1) recordcount
FROM tf_f_user WHERE user_id=TO_NUMBER(:USER_ID) AND destroy_time>(
SELECT MAX(end_date) FROM tf_f_relation_uu WHERE user_id_a=TO_NUMBER(:USER_ID)
AND user_id_b=TO_NUMBER(:USER_ID_B) AND end_date<SYSDATE)