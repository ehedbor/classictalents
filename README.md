# WoW Classic Talent Calculator
A small hobby project built with Kotlin and TornadoFX to practice application development in a practical context.
This program is a talent calculator for the original version of World of Warcraft that was re-released with WoW: Classic.

Currently, all vanilla classes are supported. In the future, I plan to add support for TBC and WotLK too.
                     
## Installation
Go to the releases tab and download the appropriate installer for your system 
(.msi for Windows, .deb for Ubuntu, etc.). Then, launch the installer and follow the instructions.

## Building from source
1. Install Java 17.
2. Clone the project with `git clone`.
3. Run the project with `./gradlew run` or create an installer with `./gradlew jpackage -PinstallerType=<msi|exe|pkg|dmg|deb|rpm>`.
                                                       
## Acknowledgements
- Wowhead, for inspiring the GUI design with [their talent calculator](https://classic.wowhead.com/talent-calc/warlock).
    Certain UI graphics were also taken from Wowhead.
- teebling, for his [WoW Classic icon pack](https://www.warcrafttavern.com/community/art-resources/icon-pack-2000-wow-vanilla-classic-icons-in-png/).
    The original artwork belongs to Blizzard Entertainment.
- Blizzard, for making a fun game

## License
This program is distributed under the MIT license. See LICENSE.txt for details.
