SELECT A.USER_ID,                                 
       A.BRAND_CODE,                             
       A.EPARCHY_CODE,                            
       A.USER_PASSWD,                           
       A.USER_STATE_CODESET,                      
       A.NET_TYPE_CODE,                         
       A.SERIAL_NUMBER,                           
       A.SCORE_VALUE,                              
       A.REMOVE_TAG
  FROM TF_F_USER A
 WHERE (:SERIAL_NUMBER is null or A.SERIAL_NUMBER=:SERIAL_NUMBER) AND
       (:USER_ID is null or A.USER_ID=:USER_ID) AND
       A.REMOVE_TAG='0'