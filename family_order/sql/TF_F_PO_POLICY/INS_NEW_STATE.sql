insert into TF_F_PO_POLICY 
(PARTITION_ID ,USER_ID      ,POSPECNUMBER ,PORATENUMBER ,START_DATE   ,END_DATE)
select PARTITION_ID ,USER_ID      ,POSPECNUMBER ,PORATENUMBER ,START_DATE   ,END_DATE
from TF_B_TRADE_POPOLICY a
where trade_id = :TRADE_ID
      AND sysdate between a.start_date and a.end_date
      and oper_code = '1'