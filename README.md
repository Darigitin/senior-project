# WTAMU Computer Science Programming Utility #

V1.6
Added visual stack coloring
Added functionally for font style/size/theme to transfer from text editor windows
Added instruction counter and error reporting areas
Made the IP editable to enable dynamic SIP
Minor Bug fixes
Code Optimization and refactoring
Documation, language reference, and syntax updates.

V1.4
Rebuilt GUI to allow for element resizing matching GUI window
Added assembly listing, file is located inside of same location as where the .jar is ran
Added support for EQU pseudo opcode
Added support for using labels inside of db pseudo opcode
Added syntax highlighting to text editor

V1.2 Updates
Store syntax corrected
Store parser now throws error when brackets are missing
rload correctly parses RSP RBP alias
db, rload, iload now parses strings correctly
Reset button now clears memory and register editing on click
rload now uses 0x0N format vs. oxN
jmpeq now parses r0 as well as R0



### What is this repository for? ###

* This program is a combination of two applications that are used in teaching students how to program and understand how programs work in a computing environment.
* Ver. 0.75.0

### How do I get set up? ###

* Visit this site to download the development environment used to create and edit this project: [NetBeans](https://netbeans.org/downloads/)  For most users, it is easier to download the version that contains the requisite version of the JDK.
* Visit this site for cloning the repository to your NetBeans installation: [Clone Instructions](https://netbeans.org/kb/docs/ide/git.html#clone)
* Requires Java 7 to compile
* Click Build to create the jar file

### What is in this version? ###

**Karel the Robot:**

* Function definitions
* All primitives: move, turnleft, pickbeeper, putbeeper, turnoff
* All checks for wall detection
* All checks for directionality
* All checks for beeper detection

**Machine Simulator:**

* All Pseudo OPs working
* All operations working
* Ability to print to a console via register F


### Future Changes ###

**Karel the Robot:**

* Ability to use the GUI in order to build the robot's world rather than only coding the world 

** Machine Simulator **

* Add disassembler
* Address screen resolution requirements that disenfranchise users with resolution challenged screens
* Address requests to move Assemble button to top for easier accessibility.
* Address issue where source editor fails to clear contents when opening a new file.

**Turing Machine**

* Requirements to be determined

### Who do I talk to? ###

* Email hhaiduk@wtamu.edu or brettdawson9@gmail.com for any questions
