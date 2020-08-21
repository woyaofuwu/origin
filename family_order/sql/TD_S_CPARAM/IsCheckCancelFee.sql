select COUNT(1) recordcount from
tf_a_payrelation a,Tf_f_Accountdeposit b
WHERE a.ACCT_ID = b.ACCT_ID
AND a.default_tag = '1'
AND a.act_tag = '1'
AND (SELECT c.acyc_id FROM td_a_acycpara c WHERE c.acyc_start_time<sysdate AND c.acyc_end_time>=sysdate) between a.start_acyc_id and a.end_acyc_id
and a.user_id = :USER_ID
and b.DEPOSIT_CODE = :DEPOSIT_CODE
and b.MONEY >= :MONEY