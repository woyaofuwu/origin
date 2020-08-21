SELECT *
From tf_f_user_sale_active
Where user_id = :USER_ID
And partition_id = Mod(:USER_ID, 10000)
And (PROCESS_TAG = :PROCESS_TAG or :PROCESS_TAG is null)
AND (accept_date >= to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss') or :START_DATE is null)
AND (accept_date <= to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss') or :END_DATE is null)
union
SELECT *
From tf_fh_user_sale_active
Where user_id = :USER_ID
And partition_id = Mod(:USER_ID, 10000)
And (PROCESS_TAG = :PROCESS_TAG or :PROCESS_TAG is null)
AND (accept_date >= to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss') or :START_DATE is null)
AND (accept_date <= to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss') or :END_DATE is null)