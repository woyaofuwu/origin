select a.acyc_id,
       a.bcyc_id,
       a.acyc_type_code,
       to_char(a.acyc_start_time, 'yyyy-mm-dd') acyc_start_time,
       to_char(a.acyc_end_time, 'yyyy-mm-dd') acyc_end_time,
       to_char(a.recv_start_time, 'yyyy-mm-dd') recv_start_time,
       to_char(a.recv_end_time, 'yyyy-mm-dd') recv_end_time,
       a.use_tag
  from td_a_acycpara a,
       (select to_char(add_months(sysdate, -3 * 12), 'yyyymm') bcyc_id
          from dual b) b,
       (select to_char(sysdate, 'yyyymm') bcyc_id from dual c) c
 where to_date(a.bcyc_id, 'yyyymm') between to_date(b.bcyc_id, 'yyyymm') and
       to_date(c.bcyc_id, 'yyyymm')
 order by bcyc_id desc