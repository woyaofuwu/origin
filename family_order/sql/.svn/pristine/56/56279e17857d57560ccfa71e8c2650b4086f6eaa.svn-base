SELECT COUNT(1) recordcount from
(
 SELECT user_id
  FROM tf_f_user_discnt b
    WHERE user_id=TO_NUMBER(:USER_ID)
      and partition_id=MOD(:USER_ID,10000)
        and end_date>trunc(last_day(sysdate))+1
         and exists
          (select 1 from td_b_product_discnt c
            where product_id=TO_NUMBER(:PRODUCT_ID)
              and c.end_date>trunc(sysdate)
               and (force_tag='1' or forcegroup_tag='1')
                and b.discnt_code=c.discnt_code
           )
         and not exists
          (select 1 from tf_b_trade_discnt c
            where trade_id=TO_NUMBER(:TRADE_ID)
              and id=TO_NUMBER(:USER_ID)
                and accept_month=TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                  and b.discnt_code=c.discnt_code
                    and c.modify_tag<>'0')
 union all
 select id from tf_b_trade_discnt b
   where trade_id=TO_NUMBER(:TRADE_ID)
     and id=TO_NUMBER(:USER_ID)
       and accept_month=TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
         and modify_tag='0'
           and end_date>trunc(last_day(sysdate))+1
             and exists
               (select 1 from td_b_product_discnt c
                  where product_id=TO_NUMBER(:PRODUCT_ID)
                    and (force_tag='1' or forcegroup_tag='1')
                      and c.end_date>trunc(sysdate)
                        and b.discnt_code=c.discnt_code
               )
)