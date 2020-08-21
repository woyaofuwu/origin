UPDATE ti_a_serv_deduct
	SET deduct_tag='1', deal_tag='3', remark='处理失败!', fail_tag='1', last_fail_time=sysdate
	WHERE user_id=TO_NUMBER(:USER_ID)