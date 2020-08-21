SELECT b.user_id,b.trade_id,b.discnt_code,b.inst_id,b.update_time ,b.modify_tag
FROM tf_b_TRADE_discnt b 
WHERE b.user_id=:USER_ID
and b.discnt_code=:DISCNT_CODE
and b.inst_id=:INST_ID
and b.modify_tag='1'
and rownum<=1
