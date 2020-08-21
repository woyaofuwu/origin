--IS_CACHE=Y
select DATA_NAME 
	   ,DATA_ID
  from td_s_static 
 where type_id = 'TD_B_BANK'
   AND DATA_ID = :BANK_CODE