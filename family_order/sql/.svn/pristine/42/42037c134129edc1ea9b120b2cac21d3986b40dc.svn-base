SELECT TO_CHAR(user_id) user_id,serial_number,bind_serial_number,rsrv_value_code,TO_CHAR(start_date,'YYYY-MM-DD HH24:MI:SS') start_date,TO_CHAR(end_date,'YYYY-MM-DD HH24:MI:SS') end_date
FROM tf_f_user_bind
WHERE user_id=TO_NUMBER(:PARAM_VALUE) AND SYSDATE BETWEEN start_date AND end_date AND :X_GETMODE=4
UNION ALL
SELECT TO_CHAR(user_id),serial_number,bind_serial_number,rsrv_value_code,TO_CHAR(start_date,'YYYY-MM-DD HH24:MI:SS'),TO_CHAR(end_date,'YYYY-MM-DD HH24:MI:SS')
FROM tf_f_user_bind
WHERE serial_number=:PARAM_VALUE AND SYSDATE BETWEEN start_date AND end_date AND :X_GETMODE=0
UNION ALL
SELECT TO_CHAR(user_id),serial_number,bind_serial_number,rsrv_value_code,TO_CHAR(start_date,'YYYY-MM-DD HH24:MI:SS'),TO_CHAR(end_date,'YYYY-MM-DD HH24:MI:SS')
FROM tf_f_user_bind
WHERE serial_number=:PARAM_VALUE AND SYSDATE BETWEEN start_date AND end_date AND :X_GETMODE=1
UNION ALL
SELECT TO_CHAR(user_id),serial_number,bind_serial_number,rsrv_value_code,TO_CHAR(start_date,'YYYY-MM-DD HH24:MI:SS'),TO_CHAR(end_date,'YYYY-MM-DD HH24:MI:SS')
FROM tf_f_user_bind
WHERE bind_serial_number=:PARAM_VALUE AND :X_GETMODE=2
UNION ALL
SELECT TO_CHAR(user_id),serial_number,bind_serial_number,rsrv_value_code,TO_CHAR(start_date,'YYYY-MM-DD HH24:MI:SS'),TO_CHAR(end_date,'YYYY-MM-DD HH24:MI:SS')
FROM tf_f_user_bind
WHERE bind_serial_number=:PARAM_VALUE AND :X_GETMODE=3