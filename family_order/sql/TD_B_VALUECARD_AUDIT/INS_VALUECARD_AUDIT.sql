INSERT INTO TD_B_VALUECARD_AUDIT(
            STAFF_ID,FEE,DEVICE_NO_S,DEVICE_NO_E,UPDATE_TIME,RSRV_STR1,
            RSRV_STR2,RSRV_STR3,RSRV_STR4,REMARK)
VALUES   (
            :VSTAFF_ID,TO_NUMBER(:VFEE),:VDEVICE_NO_S,:VDEVICE_NO_E,TO_DATE(:VUPDATE_TIME,'YYYY-MM-DDHH24:MI:SS'),:VRSRV_STR1,
            :VRSRV_STR2,:VRSRV_STR3,:VRSRV_STR4,:VREMARK
         )