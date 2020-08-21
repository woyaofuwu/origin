SELECT to_char(MAX(end_date) + 1/24/3600,'yyyy-mm-dd hh24:mi:ss') start_date 
FROM tf_f_user_discnt
WHERE PARTITION_ID = MOD(:USER_ID,10000)
AND user_id = :USER_ID
AND discnt_code = :DISCNT_CODE
AND end_date > SYSDATE