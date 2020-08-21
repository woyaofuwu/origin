select staff.STAFF_ID,
       staff.STAFF_NAME,
       passwd.STAFF_PASSWD,
       staff.CITY_CODE,
       staff.JOB_CODE,
       staff.SERIAL_NUMBER,
       staff.END_DATE,
       dept.DEPART_ID,
       dept.DEPART_CODE,
       dept.DEPART_NAME,
       dept.DEPART_KIND_CODE,
       area.AREA_CODE,
       area.AREA_NAME,
       eparchy.AREA_CODE EPARCHY_CODE,
       eparchy.AREA_NAME EPARCHY_NAME,
       staff.OPERATOR_KIND_CODE
  from TD_M_STAFF       staff,
       TD_M_DEPART      dept,
       TD_M_AREA        area,
       TD_M_AREA        eparchy,
       TF_M_STAFFPASSWD passwd
 where staff.DEPART_ID = dept.DEPART_ID
   and dept.AREA_CODE = area.AREA_CODE
   and dept.RSVALUE2 = eparchy.AREA_CODE
   and dept.VALIDFLAG = '0'
   and (dept.START_DATE is null or sysdate >= dept.START_DATE)
   and (dept.END_DATE is null or sysdate <= dept.END_DATE)
   and staff.STAFF_ID = passwd.STAFF_ID
   and staff.STAFF_ID = :STAFF_ID
   and staff.DIMISSION_TAG = :DIMISSION_TAG
   and passwd.STAFF_PASSWD = :PASSWORD