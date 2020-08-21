SELECT A.USER_ID,
   A.CUST_ID,
   A.SERIAL_NUMBER, 
   B.CUST_NAME, 
   C.USECUST_NAME,            
   A.EPARCHY_CODE,         
   C.VIP_TYPE_CODE,        
   C.CUST_TYPE,         
   C.CUST_MANAGER_ID,                 
   A.CITY_CODE,           
   to_char(sysdate,'YYYY-MM-DD HH24:MI:SS') as PORT_OUT_DATE,             
   D.GROUP_CUST_NAME, 
   D.GROUP_CUST_ID,        
   D.RSRV_STR,   
   D.GROUP_CONTACT_PHONE    
   FROM TF_F_USER A,
   TF_F_CUSTOMER B,
   (SELECT F.CUST_ID,F.VIP_TYPE_CODE,F.USECUST_NAME,F.VIP_TYPE_CODE ||'_'||F.VIP_CLASS_ID AS CUST_TYPE,F.CUST_MANAGER_ID
         FROM UOP_CRM1.TF_F_CUST_VIP F
         WHERE F.USER_ID =to_number(:USER_ID_A)
         AND F.REMOVE_TAG = '0'
         AND F.CUST_MANAGER_ID IS NOT NULL
               UNION ALL
               SELECT G.CUST_ID,G.VIP_TYPE_CODE,G.USECUST_NAME,G.VIP_TYPE_CODE ||'_'||G.VIP_CLASS_ID AS CUST_TYPE,G.CUST_MANAGER_ID 
               FROM UCR_CRM1.TF_F_CUST_VIP_SELFDEF G 
               WHERE NOT EXISTS (SELECT 1 
                                  FROM UOP_CRM1.TF_F_CUST_VIP H 
                                  WHERE G.CUST_ID=H.CUST_ID 
                                  AND H.REMOVE_TAG = '0'
                                  AND H.CUST_MANAGER_ID IS NOT NULL)
                 AND G.USER_ID =to_number(:USER_ID_B)
                 AND G.REMOVE_TAG = '0'
                 AND G.CUST_MANAGER_ID IS NOT NULL
               )C,
               ( SELECT  
               J.USER_ID,
               E.CUST_ID AS GROUP_CUST_ID,
               E.CUST_NAME AS GROUP_CUST_NAME,
               E.CUST_MANAGER_ID AS RSRV_STR, 
               H.GROUP_CONTACT_PHONE 
          FROM TF_F_CUST_GROUPMEMBER E,TF_F_CUST_GROUP H,TF_F_USER J 
          WHERE J.USER_ID =to_number(:USER_ID_C)
          AND J.USER_ID = E.USER_ID
          AND E.CUST_ID = H.CUST_ID 
          AND E.REMOVE_TAG = '0'
          AND H.REMOVE_TAG = '0'
          AND J.REMOVE_TAG = '0'  ) D     
   WHERE a.CUST_ID = B.CUST_ID      
   AND A.REMOVE_TAG = '0'           
   AND B.REMOVE_TAG = '0'         
   AND C.CUST_ID = B.CUST_ID        
   AND A.USER_ID = D.USER_ID(+)      
   AND A.USER_ID =to_number(:USER_ID_D)