select PARTITION_ID      ,USER_ID           ,SERIAL_NUMBER     ,OPR_SOURCE        ,POSPECNUMBER 
,STATUS,PRODUCTOFFERINGID,POORDERNUMBER     ,BUSINESSMODE      ,HOSTCOMPANY       ,START_DATE      
,END_DATE     
,RSRV_STR1         ,RSRV_STR2         ,RSRV_STR3         ,RSRV_STR4         , RSRV_STR5         ,RSRV_NUM1    
,RSRV_NUM2         ,RSRV_DATE1        ,RSRV_DATE2        ,     
REMARK  ,UPDATE_STAFF_ID       , UPDATE_DEPART_ID,UPDATE_TIME 
from TF_F_PO_MBMP  
where USER_ID=:USER_ID 
      and (POSPECNUMBER = :POSpecNumber or :POSpecNumber IS NULL)
      and (STATUS =:STATUS  or :STATUS IS NULL)
      and end_date>sysdate