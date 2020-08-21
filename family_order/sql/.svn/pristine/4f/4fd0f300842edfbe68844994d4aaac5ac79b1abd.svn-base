insert into TF_F_PRODUCT_POLICY 
(PARTITION_ID      ,USER_ID           ,POSPECNUMBER      ,PRODUCTSPECNUMBER ,PRODUCTID        
,PORATENUMBER             ,START_DATE        ,END_DATE  )
select PARTITION_ID      ,USER_ID           ,POSPECNUMBER      ,PRODUCTSPECNUMBER ,PRODUCTID        
,PORATENUMBER             ,START_DATE        ,END_DATE 
from  TF_B_TRADE_PRODUCTPOLICY a
where trade_id = :TRADE_ID
      AND sysdate between a.start_date and a.end_date
      and oper_code = '1'