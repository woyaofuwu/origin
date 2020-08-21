insert into TF_B_TRADE_PO_MBMP                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
   (TRADE_ID          ,PARTITION_ID      ,USER_ID           ,SERIAL_NUMBER     ,OPR_SOURCE        ,POSPECNUMBER   
   ,OPER_CODE,PRODUCTOFFERINGID,POORDERNUMBER     ,BUSINESSMODE      ,HOSTCOMPANY       ,START_DATE        ,END_DATE       
   ,RSRV_STR1         ,RSRV_STR2         ,RSRV_STR3         ,RSRV_STR4         ,RSRV_STR5         ,RSRV_NUM1      
   ,RSRV_NUM2         ,RSRV_DATE1        ,RSRV_DATE2        ,       
   REMARK  ,UPDATE_STAFF_ID       , UPDATE_DEPART_ID,UPDATE_TIME    )
values(  
   :TRADE_ID, MOD(TO_NUMBER(:USER_ID),10000),:USER_ID,           :SERIAL_NUMBER,     :OPR_SOURCE,     
   :POSpecNumber,      :OPER_CODE,         :ProductOfferingID, :POOrderNumber,     :BusinessMode,   
   :HostCompany,  TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'), TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS')        ,         :RSRV_STR1,         :RSRV_STR2,      
   :RSRV_STR3,         :RSRV_STR4,         :RSRV_STR5 ,   :RSRV_NUM1,         :RSRV_NUM2,         :RSRV_DATE1,        :RSRV_DATE2,     
   :REMARK,            :UPDATE_STAFF_ID,   :UPDATE_DEPART_ID ,:UPDATE_TIME  )