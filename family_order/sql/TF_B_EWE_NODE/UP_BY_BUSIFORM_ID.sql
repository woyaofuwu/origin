UPDATE TF_B_EWE_NODE A SET A.DEAL_STAFF_ID=:DEAL_STAFF_ID
WHERE A.BUSIFORM_ID = :BUSIFORM_ID
AND A.STATE='0'