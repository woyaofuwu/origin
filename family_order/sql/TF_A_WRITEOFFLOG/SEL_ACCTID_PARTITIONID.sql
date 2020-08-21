SELECT to_char(SUM(writeoff_fee)) writeoff_fee
  from (SELECT writeoff_fee
          FROM tf_a_writeofflog
         WHERE acct_id = TO_NUMBER(:ACCT_ID)
           and partition_id >= :START_PARITITION_ID
           and partition_id <= :END_PARITITION_ID
           AND cancel_tag = '0'
           AND operate_time >=
               TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
           AND operate_time < TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
        UNION ALL
        SELECT writeoff_fee
          FROM tf_a_writeofflog_d
         WHERE acct_id = TO_NUMBER(:ACCT_ID)
           and partition_id >= :START_PARITITION_ID
           and partition_id <= :END_PARITITION_ID
           AND cancel_tag = '0'
           AND operate_time >=
               TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
           AND operate_time < TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'))