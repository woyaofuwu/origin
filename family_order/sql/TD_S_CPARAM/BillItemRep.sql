--IS_CACHE=Y
SELECT 'BillItemRep' KEY,item_type VALUE1, bill_item_code VALUE2, bill_item VRESULT
    FROM TD_SD_BILLITEM
    WHERE 'BillItemRep'=:KEY