select sum(recordcount) recordcount
 from   (SELECT /*+ first_rows(1) */
          count(1) recordcount
         FROM   tf_b_trade_discnt
         WHERE  trade_id = TO_NUMBER(:TRADE_ID)
         AND    accept_month = TO_NUMBER(:ACCEPT_MONTH)
         AND    modify_tag in ('5', '4')
         and    rownum < 2
         union all
         SELECT /*+ first_rows(1) */
          count(1) recordcount
         FROM   tf_b_trade_svc
         WHERE  trade_id = TO_NUMBER(:TRADE_ID)
         AND    accept_month = TO_NUMBER(:ACCEPT_MONTH)
         AND    modify_tag in ('5', '4')
         and    rownum < 2
         union all
         SELECT /*+ first_rows(1) */
          count(1) recordcount
         FROM   tf_b_trade_res
         WHERE  trade_id = TO_NUMBER(:TRADE_ID)
         AND    accept_month = TO_NUMBER(:ACCEPT_MONTH)
         AND    modify_tag in ('5', '4')
         and    rownum < 2)