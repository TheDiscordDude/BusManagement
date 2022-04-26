# Bus Management

--- 

### Table of Contents 

- [Description](#description)
- [How to use](#how-to-use)

---
## Description

This is a project done as an exercise for my university. 
The program builds a bus network from the 2 files present in the folder (1_Poisy-ParcDesGlaisins.txt and 2_Piscine-Patinoire_Campus.txt).
The objective of the program is to find 3 different paths between 2 bus stops.

- The first result is the **SHORTEST** path, i.e. the path with the least amount of edges in it.
- The second result is the **FASTEST** path, i.e. the path will take the less amount of time
- The last result is what we call the **FARMOST** path. It is not the fastest not the shortest, but something in between.

If you want to see the documentation, you can see it right [there](https://thediscorddude.github.io/BusManagement/com/company/package-summary.html)

#### Technologies

To create this project, It used Java 15 SDK.
So you should be able to run it without problem with version >= 15.

---
## How to use

First, to use this tool you'll need to be in the same directory as the schedule files.
The program recognize these files if they are txt file, so keep in mind that if other '.txt' files are in the directory, the program will crash.

If you want to use the program you have to pass 2 required arguments to it : 
- The Starting point: the bus stop you want to go from
- The destination (pretty self-explanatory)

2 more arguments can be added to the command :
- the time of departure `HH-mm` i.e. `07:00`
- The Date of departure `DD-MM-YYYY` i.e. `12-04-2022`. 

Full command example :

`java -jar BusManagement.jar POISY_COLLÈGE Pommaries 10:00 30-12-2022`

On thing : There is a problem with linux that I am currently fixing.  

#### Installation

You just need to clone the project to a directory : 

`git  clone https://github.com/TheDiscordDude/BusManagement`

There is an already compiled version of the project in out/artifacts.
