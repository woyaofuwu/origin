--IS_CACHE=Y
SELECT A.STAFF_ID CUST_MANAGER_ID, A.DEPART_ID, A.STAFF_NAME CUST_MANAGER_NAME, A.JOB_CODE, a.eparchy_code, CITY_CODE
  FROM TD_M_STAFF A
 WHERE A.SERIAL_NUMBER = :SERIAL_NUMBER
   AND A.DIMISSION_TAG = 0
   AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE