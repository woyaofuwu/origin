select RELA_INST_ID,
       DECODE(B.ATTR_CODE, 'Discount', '9980168', 'BottomPrice', '9980169', B.ATTR_CODE) attr_code,
       DECODE(b.ATTR_CODE,
              '00020406',
              b.ATTR_VALUE || '000',
              '00020404',
              b.ATTR_VALUE || '000',
              '00018703',
              b.ATTR_VALUE || '000',
              '00020701',
              b.ATTR_VALUE || '000',
              '00020703',
              b.ATTR_VALUE || '000',
              '00020704',
              b.ATTR_VALUE || '000',
              '00020706',
              b.ATTR_VALUE || '000',
              '00020702',
              TO_NUMBER(b.ATTR_VALUE) * 60,
              '00020705',
              TO_NUMBER(b.ATTR_VALUE) * 60,
			  'BottomPrice',
              TO_NUMBER(b.ATTR_VALUE) * 100,
              b.ATTR_VALUE) ATTR_VALUE,
       TO_CHAR(b.START_DATE, 'YYYYMMDDHH24MISS'),
       TO_CHAR(b.END_DATE, 'YYYYMMDDHH24MISS'),
       TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),
       INST_ID,
       PARTITION_ID,
       ELEMENT_ID DISCNT_CODE,
       user_id,
       '0' ID_TYPE
  from TF_F_USER_ATTR B
 WHERE b.USER_ID = :USER_ID
   AND start_date < End_date
   AND SYSDATE < End_date
   AND B.INST_TYPE = 'D'
   AND B.ATTR_VALUE IS NOT NULL
   AND NOT EXISTS (SELECT T.PARA_CODE1
          FROM TD_S_COMMPARA T
         WHERE T.SUBSYS_CODE = 'CSM'
           AND T.PARAM_ATTR = '3994'
           AND T.PARAM_CODE = 'FILTERWLWATTRCODE'
           AND T.PARA_CODE1 = B.ATTR_CODE)
