echo "Locating Android SDK tools"
cd $HOME/Library/Android/sdk/tools/

echo "Closing all open Android emulators"
adb devices | grep emulator | cut -f1 | while read line; do adb -s $line emu kill; done

echo "Wiping Android emulator data"
emulator -avd Nexus_5_API_26 -wipe-data