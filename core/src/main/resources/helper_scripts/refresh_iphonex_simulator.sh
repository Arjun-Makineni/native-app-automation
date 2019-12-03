#!/usr/bin/env bash

uuid=$(xcrun simctl list | grep 'iPhone X ')
deviceUUID=$(echo $uuid | cut -d'(' -f 3 | cut -d')' -f 1)

echo "Using device UUID: $deviceUUID"

echo "Shutting down device"
xcrun simctl shutdown ${deviceUUID}

echo "Erasing device"
xcrun simctl erase ${deviceUUID}

echo "Booting device"
xcrun simctl boot ${deviceUUID}

echo "Launching device"
open -a Simulator --args -CurrentDeviceUDID ${deviceUUID}
