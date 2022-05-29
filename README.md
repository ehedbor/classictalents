# WoW Classic Talent Calculator
A small hobby project built with Kotlin and TornadoFX to practice application development in a practical context.
This program is a talent calculator for the original version of World of Warcraft that was re-released with WoW: Classic.
Currently, only a few of the classes are supported. In the future, I plan to support all classes across three expansions
(Vanilla, TBC, WotLK).
                     
## Installation
First, make sure to install Java 1.8.
Next, download the latest classictalents.jar from the releases tab. 
The program can be run with `java -jar classictalents-X.Y.Z.jar` (where `X.Y.Z` is the project version).


## Building from source
1. Install Java 1.8.
2. Clone the project with `git clone`.
3. In the project directory, build the project with `./gradlew :app:shadowJar`.
4. The compiled jar file will be in the subdirectory "app/build/libs"
                                                       
## Acknowledgements
- Wowhead, for inspiring the GUI design with [their talent calculator](https://classic.wowhead.com/talent-calc/warlock).
    Certain UI graphics were also taken from Wowhead.
- teebling, for his [WoW Classic icon pack](https://www.warcrafttavern.com/community/art-resources/icon-pack-2000-wow-vanilla-classic-icons-in-png/).
    The original artwork belongs to Blizzard Entertainment.
- Blizzard, for making a 

## License
This program is distributed under the GNU GPLv3. See LICENSE.txt for details.
