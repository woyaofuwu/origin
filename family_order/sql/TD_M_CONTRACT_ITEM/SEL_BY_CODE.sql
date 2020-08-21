--IS_CACHE=Y
SELECT eparchy_code,contract_item_code,contract_id,version
 FROM TD_M_CONTRACT_ITEM
 WHERE (:CONTRACT_ID is null or contract_id = :CONTRACT_ID)
 AND (:VERSION is null or version = :VERSION)
 AND (:EPARCHY_CODE is null or eparchy_code=:EPARCHY_CODE)
 AND (:CONTRACT_ITEM_CODE is null or contract_item_code=:CONTRACT_ITEM_CODE)