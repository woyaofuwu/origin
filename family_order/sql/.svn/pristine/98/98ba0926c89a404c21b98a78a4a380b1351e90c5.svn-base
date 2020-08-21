--IS_CACHE=Y
select DATA_NAME
	   ,DATA_ID
	   ,TYPE_ID
	   ,SUBSYS_CODE
  from  TD_S_STATIC 
 WHERE TYPE_ID = 'TD_S_SUPERBANK' 
   AND DATA_ID = (
                   	select PDATA_ID 
                      from TD_S_STATIC
                     WHERE TYPE_ID = 'TD_B_BANK'
                       AND DATA_ID = :BANK_CODE
 )