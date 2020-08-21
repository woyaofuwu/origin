SELECT COUNT(1) recordcount
  FROM tf_f_user_sale_active
 WHERE partition_id = mod(to_number(:USER_ID),10000)
   and user_id= to_number(:USER_ID)
   AND (PRODUCT_ID=:PRODUCT_ID OR :PRODUCT_ID='-1')
   AND rsrv_str5!=:PACKAGE_KIND_CODE
   AND process_tag=:PROCESS_TAG
   AND end_date > sysdate