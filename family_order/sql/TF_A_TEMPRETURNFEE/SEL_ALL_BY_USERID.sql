SELECT user_id,return_fee,serial_number,eparchy_code
FROM tf_a_tempreturnfee
WHERE user_id=TO_NUMBER(:USER_ID)