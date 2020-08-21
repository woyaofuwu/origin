UPDATE ti_o_deductdata
	SET deduct_tag='1', deal_tag='3', remark='处理失败!', fail_no=:FAIL_NO,deal_time=sysdate
       WHERE user_id=TO_NUMBER(:USER_ID)