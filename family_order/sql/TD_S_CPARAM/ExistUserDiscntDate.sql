SELECT COUNT(1) recordcount FROM tf_f_user_discnt a
WHERE user_id=:USER_ID
AND discnt_code=:DISCNT_CODE
AND end_date>to_date('yyyy-mm-dd hh24:mi:ss',:DATE)