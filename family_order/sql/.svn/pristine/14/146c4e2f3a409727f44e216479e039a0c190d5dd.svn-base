SELECT to_char(USER_ID) USER_ID,
       to_char(USER_ID_A) USER_ID_A,
       D.DISCNT_CODE,
       D.START_DATE DISCNT_START_DATE,
       D.END_DATE DISCNT_END_DATE,
       to_char(INST_ID) INST_ID,
       S.DISCNT_EXPLAIN,
       S.ITEM_TYPE,
       S.ITEM_CODE,
       HIGH_FEE,
       decode(S.HALF_NET, 0, '整月', 1, '半月', S.HALF_NET) HALF_NET,
       S.ADJUST_TAG,
       S.DISCNT_CODE_A,
       S.DISCNT_STATE,
       S.ENABLE_TAG,
       to_char((select max(a.end_date)
                 from tf_f_user_discnt  a,
                      td_b_discnt_share b,
                      td_s_commpara     e
                where a.user_id = TO_NUMBER(:USER_ID)
                  and a.partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
                  and a.discnt_code = b.discnt_code
                  and a.start_date < a.end_date
                  and e.param_attr = '4455'
                  and e.param_code = 'SHARE'
                  and b.discnt_code = e.para_code1
                  and sysdate < a.end_date),
               'yyyy-mm-dd hh24:mi:ss') END_DATE
  FROM tf_f_user_discnt D, TD_B_DISCNT_SHARE S, td_s_commpara a
 WHERE D.user_id = TO_NUMBER(:USER_ID)
   AND D.partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND D.DISCNT_CODE = S.DISCNT_CODE
   and a.param_attr = '4455'
   and a.param_code = 'SHARE'
   and s.discnt_code = a.para_code1
   AND D.START_DATE < D.END_DATE
   AND SYSDATE < D.END_DATE
   AND SYSDATE BETWEEN D.START_DATE AND D.END_DATE