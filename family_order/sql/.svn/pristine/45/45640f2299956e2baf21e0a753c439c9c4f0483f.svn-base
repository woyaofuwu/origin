select to_char(trade_id) trade_id, accept_month,
to_char(user_id) user_id,
 product_id, brand_code,
 old_product_id, old_brand_code,
 modify_tag,
to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,
to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
from tf_b_trade_product 
where trade_id=
            (select max(trade_id) from tf_bh_trade  b
            where b.accept_date >= trunc(sysdate,'MM')
              and b.user_id = TO_NUMBER(:USER_ID)
              and b.trade_type_code = '110'
              and b.accept_month = to_char(sysdate,'MM')
              and b.user_id = user_id         
              )
 and accept_month = to_char(sysdate,'MM')
 and brand_code='G010'