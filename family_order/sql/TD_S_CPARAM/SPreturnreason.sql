--IS_CACHE=Y
SELECT 'SPreturnreason' key, eparchy_code VALUE1,reason_code value2, reason vresult FROM  td_a_acct_mgreason WHERE 'SPreturnreason' = :key AND flag = '6'