select count(1) recordcount
  from   tf_a_payrelation a, tf_a_payrelation b
  where  a.acct_id = b.acct_id
  and    a.user_id = to_number(:USER_ID)
  and    b.user_id != to_number(:USER_ID)
  and    (select d.cycle_id from td_b_cycle d where sysdate between d.cyc_start_time and d.cyc_end_time) between b.start_cycle_id and b.end_cycle_id
  and    exists (select 1
          from   tf_f_relation_uu c
          where  c.user_id_b = to_number(:USER_ID)
          and    c.partition_id = mod(to_number(:USER_ID), 10000)
          and    c.relation_type_code = :RELATION_TYPE_CODE
          and    c.role_code_b = :ROLE_CODE_B
          and    c.end_date > sysdate)
  and    b.default_tag = '1'
  and    a.default_tag = '1'