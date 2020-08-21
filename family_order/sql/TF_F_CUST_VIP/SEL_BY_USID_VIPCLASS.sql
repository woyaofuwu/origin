SELECT TO_CHAR(a.CUST_ID) CUST_ID,
       TO_CHAR(a.USER_ID) USER_ID,
       a.SERIAL_NUMBER,
       a.VIP_CLASS_ID,
       a.VIP_TYPE_CODE
  FROM TF_F_CUST_VIP a
 WHERE user_id = :USER_ID
   AND REMOVE_TAG = :REMOVE_TAG
   AND EXISTS (SELECT 1 FROM TD_S_COMMPARA b 
                       WHERE b.SUBSYS_CODE = 'CSM' 
                       AND b.PARAM_ATTR = '8846' 
                       AND  b.PARAM_CODE = 'KAIJI' 
                       AND b.PARA_CODE1 = a.VIP_CLASS_ID )