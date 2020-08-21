SELECT distinct to_char(a.charge_id) charge_id,to_number(substr(a.charge_id,5,2)) acyc_id
  FROM tf_a_consigninfolog a,tf_a_consignlog b
 WHERE a.mconsign_id=b.consign_id 
   AND b.consign_id=to_number(:CONSIGN_ID) 
   AND a.commit_tag='1'