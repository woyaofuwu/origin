UPDATE TD_M_CONTRACT_ITEM 
   SET 
start_date = to_date(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),
end_date = to_date(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),
declare_info = :DECLARE_INFO,
remark = :REMARK,
rsvalue1 = :RSVALUE1,
rsvalue2 = :RSVALUE2,
update_time = SYSDATE,
update_staff_id = :UPDATE_STAFF_ID,
update_depart_id = :UPDATE_DEPART_ID
 WHERE (:CONTRACT_ID is null or contract_id = :CONTRACT_ID)
 AND (:VERSION is null or version = :VERSION)
 AND (:CONTRACT_ITEM_CODE is null or contract_item_code = :CONTRACT_ITEM_CODE)