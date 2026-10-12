# PadConnect

<div align="center">
        <a href="https://f-droid.org/en/packages/io.github.padconnect" target="_blank">
        <img src="https://f-droid.org/badge/get-it-on.png" alt="F-Droid">
    </a>
    <a href="https://discord.gg/BrMAZbEyXs" target="_blank">
        <img src="https://img.shields.io/discord/1496412688685858846.svg?label=&logo=discord&logoColor=ffffff&color=5865F2&labelColor=404EED" alt="Discord">
    </a>
    <a href="https://github.com/Ishan09811/PadConnect/blob/master/LICENSE" target="_blank">
        <img src="https://img.shields.io/github/license/ishan09811/PadConnect" alt="License">
    </a>
</div>


> **Low latency virtual gamepad** — built with **Kotlin** and **Jetpack Compose**

PadConnect lets your phone act as a **real controller (Xbox 360/DualShock4)** for a PC. It streams gamepad input with very low latency and exposes it on Windows using ViGEm and uinput in linux.

This project is split into two parts:

* **PadConnect** -> Android / client app (virtual controller UI)
* **[PadConnectReceiver](https://github.com/Ishan09811/PadConnectReceiver)** -> PC / receiver app (creates virtual controller)

---

## Features

* **Xbox 360(working)/DualShock4(soon) compatible** virtual controller
* **Low latency input streaming** (UDP based)
* Built with **Kotlin**
* UI powered by **Jetpack Compose**
* Supports buttons, triggers, sticks, and D-Pad
* Works over **local Wi-Fi** (no internet required)

---

## How it works

```
[ Android Phone ] ── UDP ──▶ [ PadConnectReceiver (PC) ] ──▶ [ ViGEm ] ──▶ Game
```

1. **PadConnect (Android)**

    * Renders a virtual controller using Compose
    * Captures button / axis input
    * Serializes input events
    * Sends them over UDP

2. **[PadConnectReceiver](https://github.com/Ishan09811/PadConnectReceiver)**

    * Listens for incoming UDP packets
    * Deserializes controller events
    * Feeds them into **ViGEm** in windows and **uinput** in linux
    * Exposes a virtual **Xbox 360(working)/DualShock4(soon) controller** to the OS

Games see it as a *real* controller.

---

## Tech Stack
### PadConnect (Client)

* Kotlin + Jetpack Compose
* UDP networking
* Touch -> Gamepad mapping

### [PadConnectReceiver](https://github.com/Ishan09811/PadConnectReceiver) (Server)

* Kotlin Multiplatform (KMP)/JVM
* ViGEm (Virtual Gamepad Emulation Framework)
* Xbox 360(working)/DualShock4(soon) device emulation

---

## Requirements

### Android

* Android 10+
* Wi-Fi connection

### Windows

* Windows 10 / 11
* **ViGEmBus Driver** installed
* JVM compatible environment (depending on receiver build)

### Linux
* Grant execution rights to the portable AppImage (`chmod +x PadConnectReceiver.AppImage`) and run. ensure your user profile retains permission to access `/dev/uinput`.

---

## Getting Started

### 1. Install ViGEm **(only needed for windows users)**

Download and install **ViGEmBus**:

* Official ViGEm GitHub release

Reboot after installation.

---

### 2. Run PadConnectReceiver

```bash
Windows: PadConnectReceiver.exe
Linux: PadConnectReceiver.AppImage
```

* Starts listening on UDP
* Creates a virtual controller

---

### 3. Run PadConnect (Android)

* Install the app on your phone
* Create a layout
* Start playing

---

## Supported Inputs

* Buttons: A / B / X / Y
* Shoulder buttons
* Right Analog Stick (it is mostly used for changing camera angle so emulated as touch screen like change camera angle in a mobile game)
* Dpad
* Left Analog Stick
* R3 and L3 added as separate buttons

---

## Project Structure

```
PadConnect/
 ├─ app/

PadConnectReceiver/
 ├─ data/          
 ├─ input/
 ├─ main/ui/
 ├─ native/
 ├─ utils/
 ├─ viewmodel/
```

---

## Roadmap

* [ ] DualShock4 input executor 
* [ ] Multiple controller support
* [x] Custom controller layouts
* [x] Latency stats
* [ ] BLE HID (does not require receiver)
* [x] Linux receiver
* [ ] low latency Game streaming as optional option
* [ ] USB support
* [ ] Optimise input sending
* [ ] Support for grid based controller customisation
* [ ] Support for on-screen button press haptic feedback as optional option
* [ ] improve controller haptic feedback if possible
* [ ] add right analog stick as optional option
* [x] Allow to edit entire osc(On Screen Controller) at once
* [x] don't show latency indicator text at all if disabled or not detected yet
* [x] decouple online or offline detection behaviour from latency indicator
* [x] Allow to reset controller customisations
* [ ] Make layout configurable at the moment the user creating
* [ ] Make setup screen smart to give the most latest compatible receiver version link
---

## Notes

* Works best on **local Wi-Fi** (TODO: Implement BLE HID)
* Firewall may need UDP port allowance
* ViGEm is Windows only

---

## Credits

* **ViGEm** — Virtual Gamepad Emulation Framework
* Kotlin & Jetpack Compose teams

---

## License
This project is licensed under the GNU General Public License v3.0 [(GPL-3.0-only)](https://github.com/Ishan09811/PadConnect/blob/master/LICENSE)

---

> Built for low latency, simplicity, and fun.
