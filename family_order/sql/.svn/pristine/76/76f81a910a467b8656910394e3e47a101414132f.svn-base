SELECT A.USER_ID, A.SERIAL_NUMBER, A.CUST_ID, B.CUST_NAME, C.USECUST_NAME,
       C.CUST_TYPE, C.CUST_MANAGER_ID, A.CITY_CODE, A.EPARCHY_CODE
  FROM TF_F_USER A, TF_F_CUSTOMER B,
       (SELECT F.CUST_ID, F.USECUST_NAME,
                F.VIP_TYPE_CODE || '_' || F.VIP_CLASS_ID AS CUST_TYPE,
                F.CUST_MANAGER_ID
           FROM TF_F_CUST_VIP F
          WHERE F.USER_ID = :USER_ID
            AND F.REMOVE_TAG = '0'
            AND F.CUST_MANAGER_ID IS NOT NULL
         UNION ALL
         SELECT G.CUST_ID, G.USECUST_NAME,
                G.VIP_TYPE_CODE || '_' || G.VIP_CLASS_ID AS CUST_TYPE,
                G.CUST_MANAGER_ID
           FROM TF_F_CUST_VIP_SELFDEF G
          WHERE NOT EXISTS (SELECT 1
                   FROM TF_F_CUST_VIP H
                  WHERE G.CUST_ID = H.CUST_ID
                    AND H.REMOVE_TAG = '0'
                    AND H.CUST_MANAGER_ID IS NOT NULL)
            AND G.CUST_MANAGER_ID IS NOT NULL
            AND G.USER_ID = :USER_ID
            AND G.REMOVE_TAG = '0') C, (SELECT * FROM tf_b_trade_product t, (SELECT t.data_id FROM td_s_static t WHERE t.type_id='UNIFIED_PACKAGE_JOB_PRODUCT') Y
       WHERE Y.data_id=t.product_id 
       AND t.product_id = :PRODUCT_ID
       AND t.trade_id=:TRADE_ID AND t.main_tag='1' 
       AND t.modify_tag in ('0','2') ) T
 WHERE A.CUST_ID = B.CUST_ID
   AND B.PARTITION_ID = MOD(B.CUST_ID, 10000)
   AND B.CUST_ID = C.CUST_ID
   AND A.REMOVE_TAG = '0'
   AND B.REMOVE_TAG = '0'
   AND T.USER_ID = A.USER_ID
   AND T.MODIFY_TAG = '0'
   AND A.USER_ID = :USER_ID
   AND A.PARTITION_ID = MOD(:USER_ID, 10000)
   AND T.TRADE_ID = :TRADE_ID
   AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))