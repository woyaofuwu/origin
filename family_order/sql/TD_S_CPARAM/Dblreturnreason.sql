--IS_CACHE=Y
SELECT 'Dblreturnreason' key, eparchy_code VALUE1,reason_code value2, reason vresult FROM  td_a_acct_mgreason WHERE 'Dblreturnreason' = :key AND flag = '2'