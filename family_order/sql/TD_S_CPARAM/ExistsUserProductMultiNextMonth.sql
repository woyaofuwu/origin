SELECT count(1) recordcount
  FROM tf_f_user_product a
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id = TO_NUMBER(:USER_ID)
   and end_date > sysdate
   AND EXISTS(SELECT 1 FROM td_s_commpara
              WHERE subsys_code='CSM'
                AND param_attr=:PARAM_ATTR
                AND param_code=:PARAM_CODE
                AND para_code1=to_char(a.product_id)
                AND (eparchy_code = :EPARCHY_CODE OR eparchy_code='ZZZZ')
                AND SYSDATE BETWEEN start_date AND end_date)