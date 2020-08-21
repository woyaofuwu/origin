update tf_f_user_purchase
set end_date = to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
where user_id = :USER_ID
    AND PURCHASE_ATTR = :PURCHASE_ATTR
    AND PROCESS_TAG = :PROCESS_TAG
    AND START_DATE = to_date( :START_DATE, 'yyyy-mm-dd hh24:mi:ss' )