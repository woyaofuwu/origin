--IS_CACHE=Y
SELECT 
  STAFF_ID,
  DEPART_ID,
  STAFF_NAME,
  JOB_CODE,
  MANAGER_INFO,
  SEX,
  EMAIL,
  USER_PID,
  SERIAL_NUMBER,
  CUST_ID,
  DIMISSION_TAG,
  BIRTHDAY,
  STAFF_GROUP_ID,
  CUST_HOBYY,
  MANAGER_STAFF_ID,
  RECEIVE_TYPE_CODE,
  LOGIN_FLAG,
  CUST_MANAGER_FLAG,
  CITY_CODE,
  EPARCHY_CODE,
  REMARK,
  RSVALUE1,
  RSVALUE2,
  START_DATE,
  END_DATE,
  UPDATE_TIME,
  UPDATE_STAFF_ID,
  UPDATE_DEPART_ID,
  OPERATOR_KIND_CODE,
  RSVALUE3,
  RSVALUE4
from TD_M_STAFF 
where JOB_CODE =(
select EPARCHY_CODE||'0000' 
from TD_M_STAFF
where STAFF_ID = :STAFF_ID)