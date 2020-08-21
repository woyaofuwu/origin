SELECT A.USER_ID,
        B.CUST_ID,
        A.SERIAL_NUMBER,  
        B.CUST_NAME, 
        C.USECUST_NAME , 
        C.CUST_TYPE, 
        C.CUST_MANAGER_ID,
        A.EPARCHY_CODE,
        A.CITY_CODE, 
        TO_CHAR(SYSDATE,'YYYY-MM-DD HH24:MI:SS') AS PORT_OUT_DATE,
        E.CAMPN_ID,
        E.CAMPN_NAME,
        E.START_DATE,
        E.END_DATE,
        D.GROUP_CUST_ID,
        D.GROUP_CUST_NAME,
        D.RSRV_STR1, 
        D.GROUP_CONTACT_PHONE 
        FROM TF_F_USER A,
        TF_F_CUSTOMER B,
        (SELECT F.CUST_ID,F.USECUST_NAME,F.VIP_TYPE_CODE ||'_'||F.VIP_CLASS_ID AS CUST_TYPE,F.CUST_MANAGER_ID
         FROM UOP_CRM1.TF_F_CUST_VIP F
         WHERE F.SERIAL_NUMBER =:SERIAL_NUMBER_A
         AND F.REMOVE_TAG = '0'
         AND F.CUST_MANAGER_ID IS NOT NULL
               UNION ALL
               SELECT G.CUST_ID,G.USECUST_NAME,G.VIP_TYPE_CODE ||'_'||G.VIP_CLASS_ID AS CUST_TYPE,G.CUST_MANAGER_ID 
               FROM UCR_CRM1.TF_F_CUST_VIP_SELFDEF G 
               WHERE NOT EXISTS (SELECT 1 FROM UOP_CRM1.TF_F_CUST_VIP H 
                                   WHERE G.CUST_ID=H.CUST_ID 
                                   AND H.REMOVE_TAG = '0'
                                   AND H.CUST_MANAGER_ID IS NOT NULL)
                 AND G.CUST_MANAGER_ID IS NOT NULL
                 AND G.SERIAL_NUMBER =:SERIAL_NUMBER_B
                 AND G.REMOVE_TAG = '0'
               )C,
        ( SELECT  
               J.USER_ID,
               E.CUST_ID AS GROUP_CUST_ID,
               E.CUST_NAME AS GROUP_CUST_NAME,
               E.CUST_MANAGER_ID AS RSRV_STR1, 
               H.GROUP_CONTACT_PHONE 
          FROM TF_F_CUST_GROUPMEMBER E,TF_F_CUST_GROUP H,TF_F_USER J 
          WHERE J.SERIAL_NUMBER =:SERIAL_NUMBER_C
          AND J.USER_ID = E.USER_ID
          AND E.CUST_ID = H.CUST_ID 
          AND E.REMOVE_TAG = '0'
          AND H.REMOVE_TAG = '0'
          AND J.REMOVE_TAG = '0'  ) D,         
           (SELECT A.USER_ID,
           TO_CHAR(NVL(B.RSRV_DATE1, B.START_DATE),'YYYY-MM-DD HH24:MI:SS') AS START_DATE,
           TO_CHAR(NVL(B.RSRV_DATE2, B.END_DATE),'YYYY-MM-DD HH24:MI:SS') AS END_DATE,
           B.CAMPN_ID,
           B.CAMPN_NAME
           FROM TF_F_USER A,TF_F_USER_SALE_ACTIVE B 
            WHERE A.USER_ID = B.USER_ID
            AND A.SERIAL_NUMBER = :SERIAL_NUMBER_D
            AND B.PROCESS_TAG = '0'
            AND NVL(B.RSRV_DATE2, B.END_DATE) > SYSDATE
            AND B.PARTITION_ID = MOD(A.USER_ID, 10000))E
        WHERE A.CUST_ID = B.CUST_ID       
        AND A.REMOVE_TAG = '0'            
        AND B.REMOVE_TAG = '0'   
        AND C.CUST_ID = B.CUST_ID  
        AND A.USER_ID = D.USER_ID(+)            
        AND A.USER_ID = E.USER_ID(+)  
        AND A.SERIAL_NUMBER =:SERIAL_NUMBER_E