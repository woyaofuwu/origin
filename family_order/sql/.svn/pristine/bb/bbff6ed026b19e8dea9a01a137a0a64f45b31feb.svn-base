INSERT INTO TF_F_USER_OTHER
  (PARTITION_ID,
   USER_ID,
   RSRV_VALUE_CODE,
   RSRV_VALUE,
   TRADE_ID,
   START_DATE,
   END_DATE,
   REMARK,
   INST_ID)
  SELECT MOD(:USER_ID, 10000),
         :USER_ID,
         'NET_USER_INFO',
         FIELDS1,
         :TRADE_ID,
         SYSDATE,
         TO_DATE('2050-12-31 23:59:59', 'yyyy-mm-dd hh24:mi:ss'),
         '互联网通行证号',
         f_sys_getseqid('0898','seq_inst_id')
    FROM TI_B_PF_RESULT
   WHERE TRADE_ID = :TRADE_ID
     AND BIP_CODE IN('BIP2B325_T2001325_0_0','BIP2B326_T2001326_0_0')
     AND FIELDS1 IS NOT NULL