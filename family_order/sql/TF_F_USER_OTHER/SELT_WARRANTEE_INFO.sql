WITH 
tab_warrantor AS (SELECT user_id FROM tf_F_user WHERE serial_number = to_char(:SERIAL_NUMBER) AND remove_tag = '0'),
tab_cred AS (SELECT a.user_id,a.rsrv_str6,a.rsrv_str5 FROM tf_f_user_other a,tab_warrantor b WHERE a.rsrv_value_code = 'CRED' AND a.rsrv_str6 = b.user_id AND SYSDATE BETWEEN a.start_date AND a.end_date),
tab_user AS (SELECT c.serial_number,c.cust_id,c.user_id,d.rsrv_str6,d.rsrv_str5 FROM tf_F_user c,tab_cred d WHERE c.user_id = d.user_id AND c.remove_tag = '0')
SELECT cust_name,serial_number,f.rsrv_str5 cust_type,f.rsrv_str6
FROM tf_f_customer e,tab_user f
WHERE e.cust_id=f.cust_id