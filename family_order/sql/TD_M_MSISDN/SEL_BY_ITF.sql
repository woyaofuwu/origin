--IS_CACHE=Y
SELECT SERV_TYPE, HOME_TYPE
  FROM TD_M_MSISDN
 WHERE 1 = 1
   AND :SERIAL_NUMBER BETWEEN BEGIN_MSISDN AND END_MSISDN
   AND ASP = '1'