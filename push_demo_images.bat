@echo off
echo =======================================================
echo SIPEDAS Demo Images Auto-Pusher
echo =======================================================
echo.

:: Set local path to ADB
set ADB_PATH=adb
if not exist "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" (
    echo ADB not found in default Android Sdk path. Checking PATH...
) else (
    set ADB_PATH="%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
)

echo Checking for connected emulator/device...
%ADB_PATH% devices

echo.
echo Pushing demo images to emulator's Pictures folder...
%ADB_PATH% push demo_images\antraknosa.png /sdcard/Pictures/
%ADB_PATH% push demo_images\bercak_daun.png /sdcard/Pictures/
%ADB_PATH% push demo_images\keriting_kuning.png /sdcard/Pictures/
%ADB_PATH% push demo_images\layu_fusarium.png /sdcard/Pictures/
%ADB_PATH% push demo_images\busuk_buah.png /sdcard/Pictures/

echo.
echo Scanning media files so they appear immediately in Gallery...
%ADB_PATH% shell am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file:///sdcard/Pictures/antraknosa.png
%ADB_PATH% shell am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file:///sdcard/Pictures/bercak_daun.png
%ADB_PATH% shell am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file:///sdcard/Pictures/keriting_kuning.png
%ADB_PATH% shell am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file:///sdcard/Pictures/layu_fusarium.png
%ADB_PATH% shell am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file:///sdcard/Pictures/busuk_buah.png

echo.
echo Finished! Images are now ready in your emulator Gallery.
pause
