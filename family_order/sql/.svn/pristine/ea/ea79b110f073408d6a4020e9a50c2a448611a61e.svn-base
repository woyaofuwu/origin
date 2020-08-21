select b.USER_ID_A,
       b.RELATION_TYPE_CODE || lpad(b.ROLE_CODE_B, 2, 0),
       DECODE(b.RELATION_TYPE_CODE,
              'MF',
              b.RSRV_STR1,
              DECODE(b.USER_ID_B, -1, 3, 0)),
       DECODE(b.USER_ID_B, -1, 0, b.USER_ID_B),
       b.SERIAL_NUMBER_B,
       TO_CHAR(b.START_DATE, 'YYYYMMDDHH24MISS'),
       TO_CHAR(b.END_DATE, 'YYYYMMDDHH24MISS'),
       TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),
       b.UPDATE_DEPART_ID,
       b.UPDATE_STAFF_ID,
       NVL(b.ORDERNO, 0),
       b.SERIAL_NUMBER_A,
       nvl(b.short_code, 0),
       b.role_code_a,
       b.INST_ID
 from tf_f_relation_uu b
 where b.user_id_b=:USER_ID_B
 AND b.START_DATE < b.END_DATE
 AND SYSDATE < b.END_DATE
 AND relation_type_code not in('XM','XX','XT','PW','WI')
 union all
 select b.USER_ID_A,
       b.RELATION_TYPE_CODE || lpad(b.ROLE_CODE_B, 2, 0),
       DECODE(b.RELATION_TYPE_CODE,
              'MF',
              b.RSRV_STR1,
              DECODE(b.USER_ID_B, -1, 3, 0)),
       DECODE(b.USER_ID_B, -1, 0, b.USER_ID_B),
       b.SERIAL_NUMBER_B,
       TO_CHAR(b.START_DATE, 'YYYYMMDDHH24MISS'),
       TO_CHAR(b.END_DATE, 'YYYYMMDDHH24MISS'),
       TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),
       b.UPDATE_DEPART_ID,
       b.UPDATE_STAFF_ID,
       NVL(b.ORDERNO, 0),
       b.SERIAL_NUMBER_A,
       nvl(b.short_code, 0),
       b.role_code_a,
       b.INST_ID
 from tf_f_relation_bb b
 where b.user_id_b=:USER_ID_B
 AND b.START_DATE < b.END_DATE
 AND SYSDATE < b.END_DATE
 AND relation_type_code not in('XM','XX','XT','PW','WI')