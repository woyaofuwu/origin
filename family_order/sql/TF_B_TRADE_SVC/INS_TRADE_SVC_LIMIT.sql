INSERT INTO tf_b_trade_svc(trade_id,accept_month,user_id,user_id_a,product_id,package_id,
                    service_id,main_tag,inst_id,campn_id,modify_tag,start_date,end_date)
SELECT /*+ index (b PK_TF_F_USER_SVC)*/a.trade_id,a.accept_month,b.user_id,b.user_id_a,b.product_id,b.package_id,
       b.service_id,b.main_tag,b.inst_id,b.campn_id,'1',b.start_date,a.start_date-0.00001
FROM tf_f_user_svc b,
(SELECT * FROM tf_b_trade_svc WHERE (trade_id,user_id) IN
    (SELECT trade_id,user_id FROM tf_b_trade WHERE user_id=TO_NUMBER(:USER_ID)) AND modify_tag='0') a 
WHERE b.user_id=TO_NUMBER(:USER_ID)
  AND b.PARTITION_ID=mod(TO_NUMBER(:USER_ID),10000)
  AND b.end_date>SYSDATE
  AND EXISTS (SELECT 1 FROM TD_B_ELEMENT_LIMIT
              WHERE ELEMENT_TYPE_CODE_A='S'
                AND ELEMENT_TYPE_CODE_B='S'
                AND ((ELEMENT_ID_A=a.service_id and ELEMENT_ID_B=b.service_id)
                    OR (ELEMENT_ID_A=b.service_id and ELEMENT_ID_B=a.service_id))
                AND LIMIT_TAG='0'
                AND (EPARCHY_CODE=:EPARCHY_CODE OR EPARCHY_CODE='ZZZZ')
                AND end_date>sysdate)
  AND NOT EXISTS (SELECT 1 FROM tf_b_trade_svc
                   WHERE (trade_id,user_id) IN (SELECT trade_id,user_id FROM tf_b_trade 
                                                 WHERE user_id=TO_NUMBER(:USER_ID))
                    AND modify_tag='1'
                    AND service_id=b.service_id)
UNION ALL
SELECT /*+ index (b PK_TF_F_USER_SVC)*/a.trade_id,a.accept_month,b.user_id,b.user_id_a,b.product_id,
b.package_id,b.service_id,b.main_tag,b.inst_id,b.campn_id,'1',b.start_date,a.start_date-0.00001
FROM tf_f_user_svc b,
(SELECT * FROM tf_b_trade_svc WHERE (trade_id,user_id) IN
    (SELECT trade_id,user_id FROM tf_b_trade WHERE user_id=TO_NUMBER(:USER_ID)) AND modify_tag='0') a 
WHERE b.user_id=TO_NUMBER(:USER_ID)
  AND b.PARTITION_ID=mod(TO_NUMBER(:USER_ID),10000)
  AND b.end_date>SYSDATE
  AND a.package_id=b.package_id
  AND EXISTS (SELECT 1 FROM TD_B_PACKAGE_ELEMENT_LIMIT
              WHERE PACKAGE_ID=a.package_id
                AND ELEMENT_TYPE_CODE_A='S'
                AND ELEMENT_TYPE_CODE_B='S'
                AND ((ELEMENT_ID_A=a.service_id and ELEMENT_ID_B=b.service_id)
                    OR (ELEMENT_ID_A=b.service_id and ELEMENT_ID_B=a.service_id))
                AND LIMIT_TAG='0'
                AND end_date>sysdate)
  AND NOT EXISTS (SELECT 1 FROM tf_b_trade_svc
                   WHERE (trade_id,user_id) IN (SELECT trade_id,user_id FROM tf_b_trade 
                                                 WHERE user_id=TO_NUMBER(:USER_ID))
                    AND modify_tag='1'
                    AND service_id=b.service_id)