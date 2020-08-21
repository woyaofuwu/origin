SELECT acct_id,acycid,sum(aspay_fee) aspay_fee,
count(1) RSRV_FEE1
 FROM tf_a_consigninfolog 
 WHERE recv_acyc_id=:RECV_ACYC_ID 
   AND acct_id = :ACCT_ID	 
 GROUP BY acct_id,acyc_id 
 HAVING sum(aspay_fee) > 0