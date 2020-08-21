select
  a.CREDIT_INFO_ID,
  a.PARTITION_ID,
  a.USER_ID,
  a.CREDIT_CLASS,
  a.START_DATE,
  a.END_DATE,
  a.ACT_TAG,
  a.RSRV_STR1,
  a.UPDATE_TIME
from MV_TF_O_CREDITINFO a
where a.USER_ID=:USER_ID
and   a.ACT_TAG=:ACT_TAG
and   sysdate between a.START_DATE and a.END_DATE
