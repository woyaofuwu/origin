select decode(CUST_TYPE_A, '_', CUST_TYPE_B, CUST_TYPE_A) CUST_TYPE,
          decode(USECUST_NAME_B, '', USECUST_NAME_A, USECUST_NAME_B) USECUST_NAME,
          decode(CUST_MANAGER_ID_A, '', CUST_MANAGER_ID_B, CUST_MANAGER_ID_A) CUST_MANAGER_ID
     from (select A.VIP_TYPE_CODE || '_' || A.VIP_CLASS_ID AS CUST_TYPE_A,
                  B.VIP_TYPE_CODE || '_' || B.VIP_CLASS_ID AS CUST_TYPE_B,
                  A.USECUST_NAME as USECUST_NAME_A,
                  B.USECUST_NAME as USECUST_NAME_B,
                  A.CUST_MANAGER_ID as CUST_MANAGER_ID_A,
                  B.CUST_MANAGER_ID as CUST_MANAGER_ID_B
             FROM TF_F_CUST_VIP A, Tf_f_Cust_Vip_Selfdef B
            WHERE A.CUST_ID = :CUST_ID
              and A.CUST_ID = B.CUST_ID(+)
              And A.REMOVE_TAG = :REMOVE_TAG
              and A.REMOVE_TAG = B.REMOVE_TAG(+))