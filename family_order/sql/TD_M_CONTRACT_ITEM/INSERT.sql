INSERT INTO TD_M_CONTRACT_ITEM 
(eparchy_code,contract_item_code,start_date,end_date,declare_info,time_in,staff_id_in,contract_id,version,remark,rsvalue1,rsvalue2,update_time,update_staff_id,update_depart_id)
 VALUES
(:EPARCHY_CODE,:CONTRACT_ITEM_CODE,TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),:DECLARE_INFO,SYSDATE,:STAFF_ID_IN,:CONTRACT_ID,:VERSION,:REMARK,:RSVALUE1,:RSVALUE2,TO_DATE(:UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS'),:UPDATE_STAFF_ID,:UPDATE_DEPART_ID)