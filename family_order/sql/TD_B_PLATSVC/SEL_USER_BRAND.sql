SELECT DECODE(A.BRAND_CODE,
               'G001',
               '0',
               'G002',
               '1',
               'G003',
               '1',
               'G010',
               '2',
               '3') BRAND_CODE
   FROM TF_F_USER A
  WHERE A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)
    AND A.USER_ID = :USER_ID