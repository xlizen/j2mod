# Overview
This is a project to bring the [j2mod](https://sourceforge.net/projects/j2mod/) library up to date.

j2mod has been actively maintained by [Julie Haugh](https://sourceforge.net/u/jfhaugh/) after forking it from [jamod](http://jamod.sourceforge.net/) and she has done 
absolutely sterling work in trying to iron out the myriad of wrinkles in this piece of work. It's about time she had some help so this 
project will endeavour to bring more collaboration and expertise to the fray.

The main driver for doing this work is to get away from the RxTxComm library and to use something that brings it's own native implementations that is actively supported.

The weapon of choice is the [jSerialComm](http://fazecast.github.io/jSerialComm/) library which is extremely well supported by Will Hedgecock and is actively developed.

The other goal of this project is to bring the codebase into line with JDK 1.6 and to fix all the known bugs.
## Known Issues with j2mod 1.06

### Reliance on RxTxComm
This comms library is not supported and it's installation mechanism is both antiquated and cumbersome. It has a
horrible locking mechanism and is impenetrable to most developers.

It is also hard to get binary versions created for new platforms.

### It's Not Maven
Love it or hate it, Maven underpins an enormous swathe of open source projects and it makes life a lot simpler if you can
just point to a POM and build a project.

It also makes it simpler to get the library 'out there' if it can be hosted on Maven Central.

### General Problems
* Although not necessarily a bug, the codebase likes to use a lot of in-line synchronisation on non-final objects, which at best is poor practise and at worst, unreliable.
* There's a lot of repetition in the code
* The javadoc also needs some attention - missing descriptions, incorrect tags etc.
* The format is inconsistent and in places a bit clumsy - it suffers from having originated on editors that were probably limited in width
* There is no logging framework support, all messages are written to the System.out stream
* Although there are a lot of test applications, there is no automation (junit)
* Static code analysis reveals a huge number of potential problems
* The threading system is naive and lacks proper shutdown mechanisms (relies on process death to clean up)

