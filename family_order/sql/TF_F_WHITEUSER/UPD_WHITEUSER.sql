UPDATE tf_f_whiteuser
   SET end_date=sysdate  
 WHERE pspt_type_code=:PSPT_TYPE_CODE
   AND pspt_id=:PSPT_ID
   AND eparchy_code=:EPARCHY_CODE