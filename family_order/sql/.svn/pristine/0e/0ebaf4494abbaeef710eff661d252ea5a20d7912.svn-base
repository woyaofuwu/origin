SELECT COUNT(*)  IS_FIRST                                                     
           FROM TF_F_USER_PLATSVC C                                           
          WHERE C.USER_ID = :USER_ID
            AND C.PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000)                                           
            AND C.SERVICE_ID = :SERVICE_ID                                    
            AND (  (:TIME_TYPE IS NULL) OR                                    
                   ( SYSDATE  <                                               
                   DECODE(:TIME_TYPE,                                         
                     '0', C.START_DATE +:TIME_VALUE,                          
                     '1',trunc( C.START_DATE +:TIME_VALUE ),                  
                     '2',ADD_MONTHS( C.START_DATE ,:TIME_VALUE ),             
                     '3',trunc(ADD_MONTHS( C.START_DATE ,:TIME_VALUE),'mm'),  
                     '4',ADD_MONTHS( C.START_DATE ,(:TIME_VALUE)*12),         
                     '5',trunc(ADD_MONTHS(C.START_DATE,(:TIME_VALUE)*12),'yy'),
                     SYSDATE                                                  
                   ) AND (C.BIZ_STATE_CODE='A' OR C.BIZ_STATE_CODE IS NULL))  
                 )                                                            