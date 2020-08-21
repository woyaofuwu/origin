--IS_CACHE=Y
SELECT 'FeeTypeName' KEY,'0' VALUE1,feeitem_code VALUE2,feeitem_name VRESULT
FROM td_b_feeitem
WHERE SYSDATE BETWEEN start_date AND end_date
  AND 'FeeTypeName'=:KEY
union all
SELECT 'FeeTypeName' KEY,'1' VALUE1,foregift_code VALUE2,foregift_name VRESULT
FROM td_s_foregift
WHERE 'FeeTypeName'=:KEY
union all
SELECT 'FeeTypeName' KEY,'2' VALUE1,deposit_code VALUE2,'预存款('||deposit_name||')' VRESULT
FROM td_a_depositpriorrule
WHERE 'FeeTypeName'=:KEY