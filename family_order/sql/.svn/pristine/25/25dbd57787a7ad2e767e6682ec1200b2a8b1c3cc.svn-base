SELECT *
  FROM TF_F_USER_DISCNT C
 WHERE C.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)
   AND C.USER_ID = TO_NUMBER(:USER_ID)
   AND C.DISCNT_CODE = TO_NUMBER(:DISCNT_CODE)
   AND C.END_DATE>C.START_DATE
   AND ((:PARA_CODE11 IS NULL) OR
       (SYSDATE <
       DECODE(:PARA_CODE11,
                '0',
                C.START_DATE + TO_NUMBER(:PARA_CODE12), --天
                '1',
                trunc(C.START_DATE + TO_NUMBER(:PARA_CODE12)), --自然天
                '2',
                ADD_MONTHS(C.START_DATE, TO_NUMBER(:PARA_CODE12)), --月
                '3',
                trunc(ADD_MONTHS(C.START_DATE, TO_NUMBER(:PARA_CODE12)),
                      'mm'), --自然月
                '4',
                ADD_MONTHS(C.START_DATE, TO_NUMBER(:PARA_CODE12) * 12), --年
                '5',
                trunc(ADD_MONTHS(C.START_DATE, TO_NUMBER(:PARA_CODE12) * 12),
                      'yy'), --自然年
                SYSDATE)) OR
       (SYSDATE >
       DECODE(:PARA_CODE11,
                '0',
                C.START_DATE + TO_NUMBER(:PARA_CODE12), --天
                '1',
                trunc(C.START_DATE + TO_NUMBER(:PARA_CODE12)), --自然天
                '2',
                ADD_MONTHS(C.START_DATE, TO_NUMBER(:PARA_CODE12)), --月
                '3',
                trunc(ADD_MONTHS(C.START_DATE, TO_NUMBER(:PARA_CODE12)),
                      'mm'), --自然月
                '4',
                ADD_MONTHS(C.START_DATE, TO_NUMBER(:PARA_CODE12) * 12), --年
                '5',
                trunc(ADD_MONTHS(C.START_DATE, TO_NUMBER(:PARA_CODE12) * 12),
                      'yy'), --自然年
                SYSDATE) AND
       END_DATE >
       DECODE(:PARA_CODE11,
                '0',
                sysdate - TO_NUMBER(:PARA_CODE12), --天
                '1',
                trunc(sysdate + (1 - TO_NUMBER(:PARA_CODE12))), --自然天
                '2',
                ADD_MONTHS(sysdate, (-TO_NUMBER(:PARA_CODE12))), --月
                '3',
                trunc(ADD_MONTHS(sysdate, (1 - TO_NUMBER(:PARA_CODE12))),
                      'mm'), --自然月
                '4',
                ADD_MONTHS(sysdate, (-TO_NUMBER(:PARA_CODE12)) * 12), --年
                '5',
                trunc(ADD_MONTHS(sysdate,
                                 (1 - TO_NUMBER(:PARA_CODE12)) * 12),
                      'yy'), --自然年    
                SYSDATE)))
