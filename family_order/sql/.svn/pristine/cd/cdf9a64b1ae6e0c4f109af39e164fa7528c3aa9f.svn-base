SELECT COUNT(1) recordcount FROM tf_f_user_discnt a
WHERE user_id=:USER_ID
AND discnt_code=:DISCNT_CODE
AND end_date>trunc(add_months(sysdate,1),'mm')-0.00001