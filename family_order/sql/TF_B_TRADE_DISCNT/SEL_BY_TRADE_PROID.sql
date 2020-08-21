select  TO_CHAR(a.trade_id) trade_id,
            a.accept_month,
            a.user_id,
            a.user_id_a,
            a.discnt_code,
            a.spec_tag,
            a.relation_type_code,
            a.inst_id,
            a.campn_id,
            a.start_date,
            a.end_date,
            a.modify_tag,
            a.update_time,
            a.update_staff_id,
            a.update_depart_id,
            a.remark                         
from tf_b_trade_discnt a                  
WHERE a.trade_id = TO_NUMBER(:TRADE_ID)
  AND a.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
  AND a.product_id=:PRODUCT_ID
