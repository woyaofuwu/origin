SELECT TO_CHAR(CUST_ID) CUST_ID, GROUP_ID, CUST_NAME, GROUP_TYPE, 
PNATIONAL_GROUP_NAME, MP_GROUP_CUST_CODE,
CUST_MANAGER_ID, ENTERPRISE_SCOPE
  FROM TF_F_CUST_GROUP
  WHERE GROUP_ID=:GROUP_ID
        AND REMOVE_TAG = '0'