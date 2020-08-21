--IS_CACHE=Y
SELECT 'ProductGroupBrand' KEY,product_id VALUE1,'-1' VALUE2,group_brand_code VRESULT FROM td_b_product WHERE SYSDATE BETWEEN start_date AND end_date AND 'ProductGroupBrand'=:KEY