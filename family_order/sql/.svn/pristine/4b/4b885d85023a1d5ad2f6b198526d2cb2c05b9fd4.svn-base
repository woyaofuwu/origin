UPDATE TF_F_USER_PLATSVC_ATTR A SET A.SERIAL_NUMBER=TO_NUMBER(:SERIAL_NUMBER)
                            WHERE A.USER_ID = TO_NUMBER(:USER_ID)
                            AND A.PARTITION_ID = MOD(:USER_ID,10000)
                            AND A.SERVICE_ID='98001901'
                            AND A.INFO_CODE='302'