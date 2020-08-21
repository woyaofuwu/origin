SELECT COUNT(1) recordcount FROM dual WHERE
(SELECT SUM(money) FROM tf_f_accountdeposit a WHERE acct_id=:ACCT_ID
AND EXISTS (SELECT 1 FROM td_s_commpara WHERE param_attr=716 AND param_code='07'
AND para_code1=:PARA_CODE1 AND a.deposit_code=para_code2 AND SYSDATE BETWEEN start_date AND end_date
AND (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ')))
>=:NUM