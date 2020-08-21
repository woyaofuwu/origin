SELECT a.integrate_item_code,a.integrate_item,a.eparchy_code,a.recv_acyc_id,sum(nvl(a.balance,0)) balance,sum(a.late_balance) late_balance
FROM tf_a_subconsigninfolog a,TF_A_CONSIGNINFOLOG b
WHERE b.acct_id=:ACCT_ID 
AND b.recv_acyc_id=:RECV_ACYC_ID 
AND b.acyc_id=:ACYC_ID
AND a.acct_id=b.acct_id 
AND a.bill_id=b.bill_id
GROUP BY a.integrate_item_code,a.integrate_item,a.eparchy_code,a.recv_acyc_id