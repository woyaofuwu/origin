select user_id,product_id,product_mode 
from tf_f_user_product up
where 
product_mode in ('00','01')
AND SYSDATE BETWEEN up.start_date AND up.end_date
and up.user_id = :USER_ID
and up.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
 and (:TRADE_STAFF_ID='SUPERUSR' or exists
 (select 1
          from (select b.data_code
                  from tf_m_staffdataright a, tf_m_roledataright b
                 where a.data_type = 'P'
                   and a.right_attr = 1
                   and a.right_tag = 1
                   and a.data_code = b.role_code
                   and a.staff_id = :TRADE_STAFF_ID
                union
                select a.data_code
                  from tf_m_staffdataright a
                 where a.data_type = 'P'
                   and a.right_attr = 0
                   and a.right_tag = 1
                   and a.staff_id = :TRADE_STAFF_ID) tmp
         where tmp.data_code = to_char(up.product_id)))
order by product_mode