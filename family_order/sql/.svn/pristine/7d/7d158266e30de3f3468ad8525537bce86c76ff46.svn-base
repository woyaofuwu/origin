SELECT distinct to_char(a.charge_id) charge_id ,a.acct_id acct_id
  FROM tf_a_consigninfolog a,tf_a_consignlog b
 WHERE a.mconsign_id=b.consign_id 
   AND b.consign_id=:CONSIGN_ID 
   AND a.commit_tag='1'