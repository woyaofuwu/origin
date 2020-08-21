SELECT COUNT(1) recordcount
 FROM tf_f_user_purchase a
WHERE user_id=:USER_ID
  AND NOT EXISTS(SELECT 1 FROM td_s_commpara
                  WHERE subsys_code='CSM'
                    AND param_attr=2001
                    AND param_code=:PARAM_CODE
                    AND para_code1=a.purchase_mode
                    AND (eparchy_code = :EPARCHY_CODE OR eparchy_code='ZZZZ')
                    AND SYSDATE BETWEEN start_date AND end_date)
   AND end_date>SYSDATE