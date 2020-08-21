UPDATE TF_F_USER_ATTR
SET end_date = to_date(:EXP_DATE,'YYYY-MM-DD hh24:mi:ss') ,update_time=sysdate,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID
WHERE USER_ID=TO_NUMBER(:USER_ID)
AND PARTITION_ID=MOD(TO_NUMBER(:USER_ID),10000)
AND end_date+0 >= to_date(:EXP_DATE,'YYYY-MM-DD hh24:mi:ss')
And inst_type='D'