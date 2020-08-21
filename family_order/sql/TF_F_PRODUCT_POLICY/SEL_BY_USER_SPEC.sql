select PARTITION_ID      ,USER_ID           ,POSPECNUMBER      ,PRODUCTSPECNUMBER ,PRODUCTID        
,PORATENUMBER             ,START_DATE        ,END_DATE
from TF_F_PRODUCT_POLICY
where  USER_ID=:USER_ID 
      and (POSPECNUMBER = :POSpecNumber or :POSpecNumber IS NULL)
      and (PRODUCTSPECNUMBER =:ProductSpecNumber or :ProductSpecNumber IS NULL )
      and (PORATENUMBER=:PORATENUMBER or :PORATENUMBER IS NULL )
      and end_date>sysdate