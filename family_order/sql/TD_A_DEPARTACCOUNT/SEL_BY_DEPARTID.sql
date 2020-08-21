--IS_CACHE=Y
SELECT b.depart_id,b.depart_code,b.depart_name,a.bank_code,a.bank,a.bank_acct_no,a.pay_name,a.assure_no,a.para_code1
FROM td_a_departaccount a ,td_m_depart b
WHERE b.depart_id=a.depart_id(+) AND b.depart_kind_code IN ('101','203')
AND b.depart_id = :DEPART_ID AND b.validflag='0'