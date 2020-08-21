SELECT '是' IS_SUCC,
       A.IMEI_NUMBER,
       A.STATISTICS_DATE,
       DECODE(A.EMPLOY_TYPE, '1', '语音', '2', 'GPRS', '') EMPLOY_TYPE
  FROM TF_F_TDCUST A
 WHERE A.USER_ID = (SELECT B.USER_ID
                      FROM TF_F_USER B
                     WHERE B.SERIAL_NUMBER = :SERIAL_NUMBER
                       AND B.REMOVE_TAG = '0')