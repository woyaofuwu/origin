SELECT deduct_tag,deal_tag FROM ti_o_deductdata
		WHERE user_id=TO_NUMBER(:USER_ID) AND deduct_tag='0' AND deal_tag='2'