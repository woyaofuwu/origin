select a.cust_name,d.pspt_type,a.pspt_id,c.area_name,b.manager_staff_id,
CASE WHEN a.cust_type = 0 THEN (SELECT POST_ADDRESS FROM Tf_f_Cust_Person WHERE CUST_ID = TO_CHAR(:CUST_ID) AND partition_id = MOD(to_number(:CUST_ID),10000)) ELSE (SELECT company_address FROM TF_F_CUST_GROUP WHERE CUST_ID =TO_CHAR(:CUST_ID)AND partition_id = MOD(to_number(:CUST_ID),10000)) END ADDRESS
from tf_f_customer a, tf_f_cust_group b,td_m_area c,td_s_passporttype d
where a.cust_id =  to_char(:CUST_ID) 
and b.cust_id(+) = a.cust_id
AND c.area_code = a.eparchy_code
AND d.pspt_type_code = a.pspt_type_code
AND d.eparchy_code = a.eparchy_code