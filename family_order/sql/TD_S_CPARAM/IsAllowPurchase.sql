SELECT COUNT(1) recordcount
 FROM tf_f_user_purchase
 WHERE user_id=:USER_ID
   AND process_tag=:PROCESS_TAG
   AND purchase_mode IN(select distinct purchase_mode from td_b_purchasetype
                         where sysdate between start_date and end_date
                       )
   AND end_date > sysdate
   AND ( NOT EXISTS( select 1 from td_s_commpara
                   WHERE subsys_code='CSM'
                     AND param_attr=6000
                     AND ( (param_code = :PURCHASE_MODE AND para_code1 = purchase_mode)OR
                           (param_code = purchase_mode AND para_code1 = :PURCHASE_MODE)
                         )
                     AND (eparchy_code = :EPARCHY_CODE OR eparchy_code = 'ZZZZ')
                     AND SYSDATE BETWEEN start_date AND end_date
                 )
          OR  EXISTS( select 1 from tf_f_user_purchase a
               WHERE a.user_id=:USER_ID
                 AND a.purchase_mode= :PURCHASE_MODE
                 AND a.process_tag=:PROCESS_TAG
                 AND a.end_date > sysdate
             )
      )