SELECT TO_CHAR(NVL(MAX(sum_score),0)) SUM_SCORE,TO_CHAR(NVL(MAX(vip_score),0)) VIP_SCORE
FROM
(
select sum(score) sum_score,0 vip_score
from tf_bh_trade a,tf_b_trade_score b
where trade_type_code IN (360,417)
and a.user_id=TO_NUMBER(:USER_ID)
and cancel_tag='0'
and a.trade_id=b.trade_id
UNION ALL
SELECT 0 ,score
FROM tf_f_user_newscore
WHERE user_id=TO_NUMBER(:USER_ID) AND partition_id=MOD(TO_NUMBER(:USER_ID),10000) AND id_type=2
AND acyc_id=(SELECT MAX(acyc_id)
FROM tf_f_user_newscore
WHERE user_id=TO_NUMBER(:USER_ID) AND partition_id=MOD(TO_NUMBER(:USER_ID),10000) AND id_type=2)
)