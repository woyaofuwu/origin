--IS_CACHE=Y
SELECT   
		DEPART_ID||' '||DEPART_NAME||' '||DEPART_CODE  PARA_CODE2           
		 FROM TD_M_DEPART A                                 
		WHERE RSVALUE2 = :EPARCHY_CODE                 
		  AND VALIDFLAG = '0'  
		  AND (AREA_CODE=:CITY_CODE or :CITY_CODE='HNSJ')                             
		  AND START_DATE <= SYSDATE                         
		  AND END_DATE >= SYSDATE
		  /*                           
		  AND EXISTS (SELECT 1                              
		         FROM TD_M_DEPARTKIND B                     
		        WHERE B.DEPART_KIND_CODE = A.DEPART_KIND_CODE
		          AND B.CODE_TYPE_CODE = '0')*/                
		and (DEPART_NAME like '%'||:PARA_CODE2||'%' OR DEPART_ID like '%'||:PARA_CODE2||'%' OR DEPART_CODE like '%'||:PARA_CODE2||'%')