SELECT count(1) recordcount
FROM
(
SELECT count(1) t
from tf_f_user where cust_id = :CUST_ID AND product_id = :PRODUCT_ID
UNION
select count(1) t
from tf_b_trade where cust_id = :CUST_ID AND product_id = :PRODUCT_ID AND trade_id = :TRADE_ID
) a where a.t = 1