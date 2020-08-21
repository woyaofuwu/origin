SELECT COUNT(1) recordcount FROM dual
 WHERE (
SELECT count(1) recordcount
  FROM tf_f_user
 WHERE cust_id in (SELECT cust_id
  FROM tf_f_customer
 WHERE pspt_type_code=:PSPT_TYPE_CODE
   AND pspt_id=:PSPT_ID
   AND rownum <= 30)
   AND remove_tag = '0'
   AND partition_id=MOD(user_id,10000)
   AND brand_code LIKE 'G0%'
   AND (eparchy_code=:EPARCHY_CODE OR :EPARCHY_CODE ='*')
) > :NUM