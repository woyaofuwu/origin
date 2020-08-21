UPDATE tf_f_acct_discnt
SET end_date=SYSDATE
WHERE acct_id=:ACCT_ID
AND discnt_code = 
(SELECT para_code1 from td_s_commpara where param_attr='1252' and subsys_code='CSM')
AND end_date > SYSDATE