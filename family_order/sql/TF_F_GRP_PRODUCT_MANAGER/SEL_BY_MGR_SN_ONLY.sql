SELECT T.GROUP_ID, 
       T.GROUP_MGR_SN,
       T.PRODUCT_MGR_SN,
       T.PRODUCT_MGR_NAME,
       T.DEPART_NAME,
       T.EMAIL,
       T.PRODUCT_ID
  FROM TF_F_GRP_PRODUCT_MANAGER T
 WHERE 1 = 1
   AND T.PRODUCT_MGR_SN = :PRODUCT_MGR_SN
   AND T.VALID_TAG = '0'