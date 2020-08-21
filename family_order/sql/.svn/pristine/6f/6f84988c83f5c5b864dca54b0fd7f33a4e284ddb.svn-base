update TD_B_SALE_GOODS_EXT T
set  T.PRODUCT_ID=to_number(:PRODUCT_ID)
,T.PACKAGE_ID=to_number(:PACKAGE_ID)
,T.GOODS_ID          =to_number(:GOODS_ID)     
,T.GOODS_NAME        =:GOODS_NAME      
,T.RES_ID            =:RES_ID      
,T.CITY_CODE         =:CITY_CODE    
,T.ACCOUNT_ID        =:ACCOUNT_ID      
,T.ACCOUNT_NAME      =:ACCOUNT_NAME    
,T.GOODS_PROPERTY    =:GOODS_PROPERTY  
,T.PURCHASE_TYPE     =:PURCHASE_TYPE   
,T.START_DATE        =SYSDATE      
,T.END_DATE          =TO_DATE('2050-12-31 23:59:59','yyyy-mm-dd hh24:mi:ss;')        
,T.UPDATE_TIME       =SYSDATE     
,T.UPDATE_STAFF_ID   =:UPDATE_STAFF_ID 
,T.UPDATE_DEPART_ID  =:UPDATE_DEPART_ID 
where t.EXT_ID=to_number(:EXT_ID)