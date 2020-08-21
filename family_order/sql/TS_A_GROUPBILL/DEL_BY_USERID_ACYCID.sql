DELETE FROM ts_a_groupbill
 WHERE user_id=TO_NUMBER(:USER_ID)
 AND acyc_id=:ACYC_ID