UPDATE TF_B_EOP_PRODUCT_SUB T
   SET T.VALID_TAG = :VALID_TAG
 WHERE T.IBSYSID = :IBSYSID
   AND T.RECORD_NUM = :RECORD_NUM