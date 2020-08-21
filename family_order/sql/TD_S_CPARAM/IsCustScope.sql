SELECT count(1) recordcount
FROM tf_f_cust_group t
WHERE t.CUST_ID = :CUST_ID
AND t.REMOVE_TAG='0'
AND ((t.CUST_CLASS_TYPE ='0') OR  (t.EPARCHY_CODE = :EPARCHY_CODE_B))
   AND rownum < 2