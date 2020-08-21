select a.cycle_id,
       to_char(a.cyc_start_time, 'yyyy-mm-dd') cyc_start_time,
       to_char(a.cyc_end_time, 'yyyy-mm-dd') cyc_end_time,
       to_char(a.recv_start_time, 'yyyy-mm-dd') recv_start_time,
       to_char(a.recv_end_time, 'yyyy-mm-dd') recv_end_time,
       a.use_tag
  from td_b_cycle a,
       (select to_char(add_months(sysdate, -3 * 12), 'yyyymm') cycle_id
          from dual b) b,
       (select to_char(sysdate, 'yyyymm') cycle_id from dual c) c
 where to_date(a.cycle_id, 'yyyymm') between to_date(b.cycle_id, 'yyyymm') and
       to_date(c.cycle_id, 'yyyymm')
 order by cycle_id desc