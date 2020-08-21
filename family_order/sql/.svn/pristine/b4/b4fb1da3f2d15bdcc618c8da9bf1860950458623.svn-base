SELECT *
FROM tf_f_user_sale_active b WHERE b.start_date = (SELECT max(a.start_date)
FROM tf_f_user_sale_active a 
WHERE a.user_id = :USER_ID and a.end_date < add_months(sysdate,3) AND a.end_date > sysdate) 
AND b.end_date>sysdate;