update TF_F_USER_PLATSVC_ATTR
set INFO_VALUE=:INFO_VALUE，
    UPDATE_TIME=sysdate,
    UPDATE_STAFF_ID=:UPDATE_STAFF_ID
    UPDATE_DEPART_ID=:UPDATE_DEPART_ID
where PARTITION_ID=mod(to_number(:USER_ID),10000)
and USER_ID=to_number(:USER_ID)
and SERVICE_ID=to_number(:SERVICE_ID)
and INFO_CODE=:INFO_CODE
and sysdate between start_date and end_date