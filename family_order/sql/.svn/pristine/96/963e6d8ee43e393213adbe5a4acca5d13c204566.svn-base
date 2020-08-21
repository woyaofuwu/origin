--IS_CACHE=Y
SELECT  eparchy_code,para_attr,para_code1,para_code2,
        para_name,para_value1,para_value2,para_value3,
        para_value4,para_value5,para_value6,para_value7,
        para_value8,to_char(para_value9) para_value9,
        to_char(para_value10) para_value10,
        to_char(para_value11) para_value11,
        to_char(para_value12) para_value12,
        to_char(rdvalue1,'yyyy-mm-dd hh24:mi:ss') rdvalue1,
        to_char(rdvalue2,'yyyy-mm-dd hh24:mi:ss') rdvalue2,
        to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,
        update_staff_id,update_depart_id,remark 
  FROM (SELECT  eparchy_code,para_attr,para_code1,para_code2,
                para_name,para_value1,para_value2,para_value3,
                para_value4,para_value5,para_value6,para_value7,
                para_value8,para_value9,
                para_value10,para_value11,para_value12,
                rdvalue1,rdvalue2,update_time,
                update_staff_id,update_depart_id,remark
           FROM TD_M_RES_PARA
          WHERE  eparchy_code=:EPARCHY_CODE
            AND para_attr=:PARA_ATTR
            AND para_value2=:PARA_VALUE2
          ORDER BY para_code1 ASC
        )
 WHERE :SERIAL_NUMBER LIKE para_value1
   AND ROWNUM < 2