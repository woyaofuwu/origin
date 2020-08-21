UPDATE TF_B_TRADE_DISCNT
SET MODIFY_TAG = :MODIFY_TAG 
WHERE  TRADE_ID = to_number(:TRADE_ID)
  AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
  AND USER_ID = to_number(:USER_ID)
  AND PACKAGE_ID = :PACKAGE_ID
  AND PRODUCT_ID = :PRODUCT_ID
  AND DISCNT_CODE = :DISCNT_CODE
  AND INST_ID = :INST_ID