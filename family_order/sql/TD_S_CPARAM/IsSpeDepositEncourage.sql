SELECT COUNT(1) recordcount
  FROM  tf_f_specific_discnt a
 WHERE id=:USER_ID
   AND id_type_code='0'
   AND (disn_type_code=10015 OR
        EXISTS (SELECT 1 FROM td_s_commpara
                 WHERE param_attr=78 AND para_code3=a.disn_type_code
                   AND SYSDATE BETWEEN start_date AND end_date AND (eparchy_code=:TRADE_EPARCHY_CODE OR eparchy_code='ZZZZ')))
   AND end_acyc_id >=(SELECT acyc_id FROM td_a_acycpara WHERE SYSDATE BETWEEN acyc_start_time AND acyc_end_time-1/24/3600)