   SELECT  
    a.CUST_NAME 
  FROM tf_f_customer a
 WHERE 1=1
   AND a.pspt_type_code=:PSPT_TYPE_CODE
   AND a.pspt_id=:PSPT_ID
   AND a.CUST_ID <> :CUST_ID
   and a.cust_type='0'
   AND a.CUST_NAME<>:PSPT_NAME
   AND a.CUST_NAME is not null 
   AND a.REMOVE_TAG='0'
   and  exists (select 1 from tf_f_user b where b.cust_id=a.cust_id and b.remove_tag='0')