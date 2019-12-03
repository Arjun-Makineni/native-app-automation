#!/usr/bin/env bash

uuid=$(xcrun simctl list | grep 'iPad (5th generation)')
deviceUUID=$(echo $uuid | cut -d'(' -f 5 | cut -d')' -f 1)

echo "Using device UUID: $deviceUUID"

echo "Shutting down device"
xcrun simctl shutdown ${deviceUUID}

echo "Erasing device"
xcrun simctl erase ${deviceUUID}

echo "Booting device"
xcrun simctl boot ${deviceUUID}

# ----- Un-comment if you want to manually install the app -----
# echo "Installing app"
# xcrun simctl install booted "<app-path>"

echo "Launching device"
open -a Simulator --args -CurrentDeviceUDID ${deviceUUID}