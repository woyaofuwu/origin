SELECT count(1) recordcount
  FROM tf_f_user_discnt WHERE user_id IN (
SELECT user_id FROM tf_f_user WHERE cust_id =:CUST_ID AND product_id = 8000 AND remove_tag = '0')
AND discnt_code IN (SELECT para_code1 FROM td_s_commpara
              WHERE subsys_code='CSM'
                AND param_attr=2001
                AND param_code=:PARAM_CODE
                AND (eparchy_code = :EPARCHY_CODE OR eparchy_code='ZZZZ')
                AND SYSDATE BETWEEN start_date AND end_date)
AND end_date > sysdate