#!/usr/bin/env bash
# Builds, installs, and launches Bearings on the projected AI Glasses display.
#
# Why this script exists: Android Studio's Run button launches on display 0
# (the phone's own screen), which the system rejects for GlassesActivity
# (it requires the XR_PROJECTED display category). The app must instead be
# launched with --display targeting the phone's ProjectionDisplayRestricted
# virtual display, whose numeric id changes every emulator session. This
# script detects that id automatically instead of requiring you to.
#
# Prerequisites: both the phone AVD and the Display Glasses AVD are already
# running and paired (via the Glasses Pairing Assistant notification on the
# phone). Run this from the repo root.

set -euo pipefail

if ! command -v adb &> /dev/null; then
  echo "error: adb not found on PATH. Add your Android SDK platform-tools directory, e.g.:" >&2
  echo "  export PATH=\"\$PATH:\$HOME/Library/Android/sdk/platform-tools\"" >&2
  exit 1
fi

echo "Looking for the phone emulator hosting the projected glasses display..."

PHONE_SERIAL=""
DISPLAY_ID=""
for serial in $(adb devices | awk 'NR>1 && $2=="device" {print $1}'); do
  match=$(adb -s "$serial" shell dumpsys display 2>/dev/null \
    | grep -oE "displayId=[0-9]+, uniqueId='virtual:[^']*ProjectionDisplayRestricted[^']*'" \
    | head -1)
  if [ -n "$match" ]; then
    PHONE_SERIAL="$serial"
    DISPLAY_ID=$(echo "$match" | grep -oE "displayId=[0-9]+" | grep -oE "[0-9]+")
    break
  fi
done

if [ -z "$PHONE_SERIAL" ]; then
  echo "error: no connected device has a ProjectionDisplayRestricted display." >&2
  echo "Make sure both the phone AVD and the Display Glasses AVD are running" >&2
  echo "and paired via the Glasses Pairing Assistant, then try again." >&2
  exit 1
fi

echo "Found it: phone emulator $PHONE_SERIAL, glasses display id $DISPLAY_ID"
echo ""
echo "Building..."
./gradlew assembleDebug -q

echo "Installing on $PHONE_SERIAL..."
adb -s "$PHONE_SERIAL" install -r app/build/outputs/apk/debug/app-debug.apk

echo "Launching on the projected glasses display..."
adb -s "$PHONE_SERIAL" shell am force-stop com.bearings
adb -s "$PHONE_SERIAL" shell am start -n com.bearings/.GlassesActivity --display "$DISPLAY_ID"

echo ""
echo "Done — check the Display Glasses emulator window."
