--IS_CACHE=Y
SELECT to_char(user_type_code) user_type_code,to_char(user_type) user_type,to_char(eparchy_code) eparchy_code
FROM td_b_usertype
WHERE user_attr='0' AND SYSDATE BETWEEN start_date AND end_date
order by user_type_code