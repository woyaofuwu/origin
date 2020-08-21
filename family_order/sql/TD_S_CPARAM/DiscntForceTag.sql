--IS_CACHE=Y
SELECT 'DiscntForceTag' KEY,product_id VALUE1,discnt_code VALUE2,force_tag VRESULT
FROM td_b_product_discnt
WHERE force_tag='1' AND SYSDATE BETWEEN start_date AND end_date
AND 'DiscntForceTag'=:KEY