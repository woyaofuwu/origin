SELECT COUNT(1) recordcount FROM dual
WHERE (
Select Count(1) From tf_f_user a Where
  cust_id= :CUST_ID
  And brand_code like 'G%'
  And remove_tag='0'
  ) > :NUM