UPDATE TD_S_NONBOSSPARA T
   SET  T.DATA_NAME          =:DATA_NAME     
		   ,T.PARAM_NAME         =:PARAM_NAME                            
		   ,T.PARA_CODE1         =:PARA_CODE1                            
		   ,T.PARA_CODE2         =:PARA_CODE2                            
		   ,T.PARA_CODE3         =:PARA_CODE3                            
		   ,T.PARA_CODE4         =:PARA_CODE4                            
		   ,T.PARA_CODE5         =:PARA_CODE5                            
		   ,T.PARA_CODE6         =:PARA_CODE6                            
		   ,T.PARA_CODE7         =:PARA_CODE7                            
		   ,T.PARA_CODE8         =:PARA_CODE8                            
		   ,T.PARA_CODE9         =:PARA_CODE9                            
		   ,T.PARA_CODE10        =:PARA_CODE10                           
		   ,T.PARA_CODE11        =:PARA_CODE11                           
		   ,T.PARA_CODE12        =:PARA_CODE12                           
		   ,T.PARA_CODE13        =:PARA_CODE13                           
		   ,T.PARA_CODE14        =:PARA_CODE14                           
		   ,T.PARA_CODE15        =:PARA_CODE15                           
		   ,T.PARA_CODE16        =:PARA_CODE16                           
		   ,T.PARA_CODE17        =:PARA_CODE17                           
		   ,T.PARA_CODE18        =:PARA_CODE18                           
		   ,T.PARA_CODE19        =:PARA_CODE19                           
		   ,T.PARA_CODE20        =:PARA_CODE20                           
		   ,T.PARA_CODE21        =:PARA_CODE21                           
		   ,T.PARA_CODE22        =:PARA_CODE22                           
		   ,T.PARA_CODE23        =:PARA_CODE23                           
		   ,T.PARA_CODE24        =:PARA_CODE24                           
		   ,T.PARA_CODE25        =:PARA_CODE25                           
		   ,T.PARA_CODE26        =to_date(:PARA_CODE26, 'yyyy-mm-dd')     
		   ,T.PARA_CODE27        =to_date(:PARA_CODE27, 'yyyy-mm-dd')     
		   ,T.PARA_CODE28        =to_date(:PARA_CODE28, 'yyyy-mm-dd')     
		   ,T.PARA_CODE29        =to_date(:PARA_CODE29, 'yyyy-mm-dd')     
		   ,T.PARA_CODE30        =to_date(:PARA_CODE30, 'yyyy-mm-dd')     
		   ,T.START_DATE         =case when :START_DATE is not null
															   then
															   to_date(:START_DATE || ' 00.00.00', 'yyyy-mm-dd hh24:mi:ss')    
															   else
															   null
															   end
		   ,T.END_DATE           =case when :END_DATE is  null
														   then
														   to_date('2050-12-31' || ' 23.59.59', 'yyyy-mm-dd hh24:mi:ss')
														   else
														   to_date(:END_DATE || ' 23.59.59', 'yyyy-mm-dd hh24:mi:ss')
														   end       
		   ,T.EPARCHY_CODE       ='0898'                                 
		   ,T.UPDATE_STAFF_ID    =:UPDATE_STAFF_ID                       
		   ,T.UPDATE_DEPART_ID   =:UPDATE_DEPART_ID                      
		   ,T.UPDATE_TIME        =sysdate                                
		   ,T.REMARK             =:REMARK
 WHERE T.TYPE_ID = :TYPE_ID
   AND T.DATA_ID = :DATA_ID_OLD 