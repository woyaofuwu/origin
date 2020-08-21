--IS_CACHE=Y
SELECT depart_id,depart_code,bank_code,bank,bank_acct_no,pay_name
  FROM td_a_departaccount
 WHERE depart_id=:DEPART_ID