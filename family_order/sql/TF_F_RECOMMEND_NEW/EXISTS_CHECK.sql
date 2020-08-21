SELECT :USER_ID user_id
  FROM dual
 WHERE (:ID_TYPE = '0' AND EXISTS (SELECT 1 FROM tf_f_user_infochange 
                                     WHERE user_id = :USER_ID 
                                       AND partition_id = MOD(:USER_ID,10000)
                                       AND ','||:ID||',' LIKE '%,'||product_id||',%'
                                       AND end_date+0 >= SYSDATE
                                   )
       )
    OR (:ID_TYPE = '1' AND EXISTS (SELECT 1 FROM tf_f_user_svc 
                                     WHERE user_id = :USER_ID  
                                       AND partition_id = MOD(:USER_ID,10000)
                                       AND ','||:ID||',' LIKE '%,'||service_id||',%'
                                       AND end_date+0 >= SYSDATE
                                   )
       )
    OR (:ID_TYPE = '2' AND EXISTS (SELECT 1 FROM tf_f_user_discnt 
                                     WHERE user_id = :USER_ID  
                                       AND partition_id = MOD(:USER_ID,10000)
                                       AND ','||:ID||',' LIKE '%,'||discnt_code||',%'
                                       AND end_date+0 >= SYSDATE
                                   )
       )
    OR (:ID_TYPE = '4' AND EXISTS (SELECT 1 FROM tf_f_user_mbmp_sub 
                                     WHERE user_id = :USER_ID 
                                       AND sp_id = '801234'
                                       AND sp_svc_id = '110301'
                                       AND biz_state_code = 'A'
                                       AND end_date+0 >= SYSDATE
                                   )
       )