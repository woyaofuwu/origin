UPDATE TF_F_USER_ATTR
SET end_date = TRUNC(LAST_DAY(to_date(:FINISH_DATE, 'yyyy-mm-dd hh24:mi:ss')))+1-1/24/3600 ,update_time=to_date(:FINISH_DATE,'yyyy-mm-dd hh24:mi:ss'),update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID
WHERE USER_ID=TO_NUMBER(:USER_ID)
AND PARTITION_ID=MOD(TO_NUMBER(:USER_ID),10000)
AND end_date+0 >= TRUNC(LAST_DAY(SYSDATE))+1
And inst_type='D'