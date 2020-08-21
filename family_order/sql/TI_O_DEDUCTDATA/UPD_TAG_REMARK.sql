UPDATE ti_o_deductdata
SET deduct_tag='1', deal_tag='1', remark='处理成功!', fail_no =0,deal_time=sysdate
WHERE user_id=TO_NUMBER(:USER_ID)