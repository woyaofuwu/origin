SELECT COUNT(1) RECORDCOUNT FROM TF_F_USER U
WHERE 1=1
AND U.CUST_ID = :CUST_ID
AND U.PRODUCT_ID = :PRODUCT_ID    
AND U.REMOVE_TAG = '0'