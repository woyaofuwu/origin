UPDATE TF_F_USER_COMP_RELA
SET ROLE ='1'
WHERE USER_ID =TO_NUMBER(:USER_ID)
AND COMP_PRODUCT_ID=:COMP_PRODUCT_ID
AND COMP_USER_ID=TO_NUMBER(:COMP_USER_ID)
AND RELATION_TYPE_CODE=:RELATION_TYPE_CODE
AND EPARCHY_CODE=:EPARCHY_CODE
AND END_DATE > SYSDATE