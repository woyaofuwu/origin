--IS_CACHE=Y
SELECT 'ProductName' KEY,product_id VALUE1,'-1' VALUE2,product_name VRESULT FROM td_b_product WHERE SYSDATE BETWEEN start_date AND end_date AND 'ProductName'=:KEY