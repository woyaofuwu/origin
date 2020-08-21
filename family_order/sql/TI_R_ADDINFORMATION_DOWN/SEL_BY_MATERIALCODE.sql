SELECT RECORDID,TRANSTYPE,TIMESTAMP,MATERIALCODE,BRANDABBREVIATION,NAMEABBREVIATION,TERMINALCATALOG,FRONTPIC,BACKPIC,SIDEPIC,OTHERPIC,MARKETDATE,
APPEARANCE,MOBILESIZE,WEIGHT,MAINSCREENSIZE,MAINSCREENQUALITY,MAINSCREENCOLOR,MAINSCREENPARAMETER,TOUCHSCREEN,NETWORK,OPERSYSTEM,CPUBRAND,
CPUMODEL,CPURATE,GPU,RAMVOLUME,BODYVOLUME,EXPANDVOLUME,STORGECADETYPE,KEYBOARDTYPE,BATTERYVOLUME,CALLDURATION,INPUTMETHOD,INPUTWAY,
ADDRESSBOOK,CALLRECORD,OTHERFEATURE,MOBILEPORTAL,MOBILEREADING,MOBILEMUSIC,MOBILEGAME,MOBILEPAYMENT,MOBILEVIDEO,MOBILETV,MOBILENAVIGATION,
MOBILECATOON,MOBILEMARKET,MAILBOX139,ELECTRONICCOMPASS,GRAVITYINDUCTOR,ACCELERATIONSENSOR,LIGHTSENSOR,DISTANCESENSOR,ACCELERATION3D,
VIDEOSHOOT,VIDEOPLAY,AUDIOPLAY,GRAPHMODE,RADIO,FLASHPLAY,SNS,IMMEDIATEMESSAGER,BLUETOOTH,WLAN,DIGITALLINTERFACE,HEADSETJACK,CYBERMAIL,
OFFICETOOLS,FLIGHTMODE,WORLDTIME,DATABACKUP,DATAENCRYPTION,REMOTECONTROL,MEMORANDUM,CALENDAR,MAINFRAME,LIBATTERY,DATALINE,HEADSET,
RECHARGER,SPECIFICATION,REMARK 
FROM TI_R_ADDINFORMATION_DOWN
WHERE 1=1
     AND DEALFLAG =:DEALFLAG
     AND RECORDDATE <= TO_DATE(:END_DATE, 'yyyy-mm-dd hh24:mi:ss')