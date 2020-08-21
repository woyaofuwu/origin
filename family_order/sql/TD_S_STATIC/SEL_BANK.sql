--IS_CACHE=Y
select data_id as KEY_1
	   ,data_name as VALUE_1
	   ,PDATA_ID AS PDATA
	   ,EPARCHY_CODE AS CODE
  from td_s_static
 WHERE TYPE_ID = 'TD_B_BANK'
   AND EPARCHY_CODE =:EPARCHY_CODE