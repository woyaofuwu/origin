--IS_CACHE=Y
SELECT 'SPDbreturnreason' key, eparchy_code VALUE1,reason_code value2, reason vresult FROM  td_a_acct_mgreason WHERE 'SPDbreturnreason' = :key AND flag = '7'