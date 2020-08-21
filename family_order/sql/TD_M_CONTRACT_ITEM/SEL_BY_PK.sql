--IS_CACHE=Y
SELECT eparchy_code,contract_item_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,declare_info,to_char(time_in,'yyyy-mm-dd hh24:mi:ss') time_in,staff_id_in,contract_id,version,remark,rsvalue1,rsvalue2,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,0 x_tag
 FROM TD_M_CONTRACT_ITEM
 WHERE (:CONTRACT_ID is null or contract_id = :CONTRACT_ID)
 AND (:VERSION is null or version = :VERSION)