UPDATE TF_F_USER_PLATSVC A SET A.SERIAL_NUMBER=TO_NUMBER(:SERIAL_NUMBER)
                            WHERE A.USER_ID = TO_NUMBER(:USER_ID)
                            AND A.PARTITION_ID = MOD(:USER_ID,10000)
                            AND A.BIZ_TYPE_CODE='19'
                            AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE