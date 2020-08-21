SELECT to_char(first_fail_time,'YYYYMMDD') first_fail_time,to_char(last_fail_time,'YYYYMMDD') last_fail_time,fail_tag
			FROM ti_a_serv_deduct
			WHERE user_id=TO_NUMBER(:USER_ID)