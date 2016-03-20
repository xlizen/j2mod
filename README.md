# Overview
This is a project to bring the [j2mod](https://sourceforge.net/projects/j2mod/) library into the 21st century.

j2mod has been actively maintained by [Julie Haugh](https://sourceforge.net/u/jfhaugh/) after forking it from [jamod](http://jamod.sourceforge.net/) and she has done 
absolutely sterling work in trying to iron out the myriad of wrinkles in this piece of work. It's about time she had some help so this 
project will endeavour to bring more collaboration and expertise into the fray.

The main driver for doing this work is to get away from the RxTxComm library and to use something that brings it's own native implementations and is actively supported.

The weapon of choice is the [jSerialComm](http://fazecast.github.io/jSerialComm/) library.

The other goal of this project is to bring the codebase into line with JDK 1.6 and to fix all the known bugs.
## Known Issues with j2mod

### Reliance on RxTxComm
This library is not supported and it's installation mechanism is antiquated and cumbersome. It has a
horrible locking mechanism and is impenetrable.

It is also hard to get binary versions created for all the different platforms.

### It's Not Maven
Love it or hate it, maven underpins an enormous swathe of open source projects and it makes life a lot simpler if you can
just point to a POM and build a project.

### ThreadPool
Should be replaced by JVM ThreadPool with proper init and close code in the TCPListener that uses it

### General
* Although not necessarily a bug, the codebase likes to use a lot of in-line synchronisation on non-final objects, which at best is poor practise and at worst, unreliable.
* There's a lot of repetition in the code
* The javadoc also needs some attention - missing descriptions, incorrect tags etc.
* The format is inconsistent and in places a bit clumsy - it suffers from having originated on editors that were probably limited in width
* There is no logging framework support, all messages are written to the System.out stream
* Although there are a lot of test applications, there is no automation (junit)
* Static code analysis reveals a huge number of potential problems
