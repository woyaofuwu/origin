select PARTITION_ID       ,USER_ID            ,POSPECNUMBER  
,PRODUCTSPECNUMBER  ,PRODUCTID        ,STATUS          ,BIZ_CODE           ,RB_LIST           
,RSRV_STR1     
,RSRV_STR2          ,RSRV_STR3  ,RSRV_STR4,  RSRV_STR5        ,RSRV_NUM1          ,RSRV_NUM2  ,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5        ,RSRV_DATE1        
,RSRV_DATE2 ,RSRV_DATE3   
,START_DATE         ,END_DATE,REMARK  ,UPDATE_STAFF_ID       , UPDATE_DEPART_ID,UPDATE_TIME 
from TF_F_PO_MBMP_SUB  
where USER_ID=:USER_ID 
      and (POSPECNUMBER = :POSpecNumber or :POSpecNumber IS NULL)
      and (PRODUCTSPECNUMBER =:ProductSpecNumber or :ProductSpecNumber IS NULL )
      and (status=:STATUS or :STATUS IS NULL)
      and end_date>sysdate