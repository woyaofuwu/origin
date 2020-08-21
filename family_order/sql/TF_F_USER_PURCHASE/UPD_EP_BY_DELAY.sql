update tf_f_user_purchase
set end_date = add_months( trunc(last_day(SYSDATE)+1)-1/24/60/60, decode(left_months, '', 0,left_months) )
where user_id = to_number(:USER_ID)
    AND PURCHASE_ATTR = :PURCHASE_ATTR
    AND PROCESS_TAG = :PROCESS_TAG
    AND START_DATE = to_date( :START_DATE, 'yyyy-mm-dd hh24:mi:ss' )
    and (:END_DATE=:END_DATE or :END_DATE IS NULL )