SELECT PRODUCT_NAME,NVL(RSRV_DATE2,END_DATE) END_DATE
FROM tf_f_user_sale_active b WHERE NVL(RSRV_DATE1,START_DATE) = (SELECT max(NVL(RSRV_DATE1,START_DATE))
FROM tf_f_user_sale_active a 
WHERE a.user_id = :USER_ID )  AND b.user_id = :USER_ID
AND NVL(RSRV_DATE2,END_DATE) < add_months(sysdate,3) AND PARTITION_ID = MOD(:USER_ID, 10000) order by NVL(RSRV_DATE2,END_DATE) desc