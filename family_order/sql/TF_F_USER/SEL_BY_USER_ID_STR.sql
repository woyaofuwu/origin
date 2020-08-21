SELECT  serial_number  FROM tf_f_user
 WHERE user_id=TO_NUMBER(:USER_ID)
  AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)
  AND remove_tag='0'