select DISTINCT PRODUCT_ID,  PACKAGE_ID
from  TF_F_USER_GRP_PACKAGE
where  USER_ID=TO_NUMBER(:USER_ID) AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)  AND sysdate BETWEEN START_DATE AND END_DATE