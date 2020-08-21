select remark, sum(money) limit_money
  from (select deposit_code, money, eparchy_code
          from tf_a_accesslog
         WHERE acct_id = TO_NUMBER(:ACCT_ID)
           AND partition_id >= :START_PARTITION_ID
           AND partition_id <= :END_PARTITION_ID
           AND access_tag = :ACCESS_TAG
           AND cancel_tag = '0'
           AND operate_time >=
               TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
           AND operate_time <= TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
        union all
        select deposit_code, money, eparchy_code
          from tf_a_accesslog_d
         WHERE acct_id = TO_NUMBER(:ACCT_ID)
           AND partition_id >= :START_PARTITION_ID
           AND partition_id <= :END_PARTITION_ID
           AND access_tag = :ACCESS_TAG
           AND cancel_tag = '0'
           AND operate_time >=
               TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
           AND operate_time <= TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')) a,
       td_s_tag b
 where a.eparchy_code = b.eparchy_code
   AND a.DEPOSIT_CODE = b.tag_number
   AND b.tag_code like 'ASM_SMALLTAB_DEPOSIT%'
 group by b.remark