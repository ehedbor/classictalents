# WoW Classic Talent Calculator
This program is a talent calculator for the original version of World of Warcraft 
that was re-released with WoW: Classic. The tool supports every class and 
specialization in the Classic, TBC and WotLK expansions.

Currently, glyphs are not supported, but they might be added at a later date. Maybe.

## Installation
Go to the releases tab and download the appropriate installer for your system 
(.msi for Windows, .deb for Ubuntu, etc.). Then, launch the installer and follow the instructions.

## Building from source
1. Install Java 17.
2. Clone the project with `git clone`.
3. Run the project with `./gradlew run` or create an installer with
   `./gradlew jpackage -PinstallerType=<msi|exe|pkg|dmg|deb|rpm>`.
                                                       
## Acknowledgements
- Wowhead, for inspiring the GUI design with [their talent calculator](https://classic.wowhead.com/talent-calc).
  Additionally, the data and icons for the talents were scraped from their website.
  (If only I had thought to do that before way spending too many hours of my life  
  manually writing and verifying data files. Oh well!)
- Blizzard, for making a fun game

## License
This program is distributed under the MIT license. See LICENSE.txt for details.
