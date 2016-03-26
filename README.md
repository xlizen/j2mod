# Overview
This is a project to bring the [j2mod](https://sourceforge.net/projects/j2mod/) library up to date.

j2mod has been actively maintained by [Julie Haugh](https://sourceforge.net/u/jfhaugh/) after forking it from [jamod](http://jamod.sourceforge.net/) and she has done 
absolutely sterling work in trying to iron out the myriad of wrinkles in this piece of work. It's about time she had some help so this 
project will endeavour to bring more collaboration and expertise to the fray.

The main driver for doing this work is to get away from the RxTxComm library and to use something that brings it's own native implementations that is actively supported.

The weapon of choice is the [jSerialComm](http://fazecast.github.io/jSerialComm/) library which is extremely well supported by Will Hedgecock and is actively developed.

The other goal of this project is to bring the codebase into line with JDK 1.6 and to fix all the known bugs.
## Known Issues with j2mod 1.6

### Reliance on RxTxComm
This library is not supported and it's installation mechanism is antiquated and cumbersome. It has a
horrible locking mechanism and is impenetrable.

It is also hard to get binary versions created for new platforms.

### It's Not Maven
Love it or hate it, Maven underpins an enormous swathe of open source projects and it makes life a lot simpler if you can
just point to a POM and build a project.

It also makes it simpler to get the library 'out there' if it can be hosted on Maven Central.

### General
* Although not necessarily a bug, the codebase likes to use a lot of in-line synchronisation on non-final objects, which at best is poor practise and at worst, unreliable.
* There's a lot of repetition in the code
* The javadoc also needs some attention - missing descriptions, incorrect tags etc.
* The format is inconsistent and in places a bit clumsy - it suffers from having originated on editors that were probably limited in width
* There is no logging framework support, all messages are written to the System.out stream
* Although there are a lot of test applications, there is no automation (junit)
* Static code analysis reveals a huge number of potential problems
* The threading system is naive and lacks proper shutdown mechanisms (relies on process death to clean up)

## Version 2.0-rc1
* RxTxComm has been successfully replaced with jSerialComm and tested with RTU
* The whole codebase has been re-vamped to use modern constructs and practises, not a single class has remained untouched
* Codebase is compatible with JDK 1.6
* Javadoc has been extensively improved and fixed
* A logging framework (log4j) has been included with a wrapper to improve performance and allow token passing
* We have some tests!! Unit tests have been added for all automation friendly protocols (UDP and TCP)
* Unit tests have been added that independently verify the j2mod TCP Slave functionality
* Threading and concurrency has been hugely improved - removed synchronisation errors and corrected the handling of thread shutdown
* Brought some sanity to the Facade package so that it is now usable in a production setting
* Removed the serial BIN transport - it was proprietary and not taken up by any hardware vendors that I could find
* Fixed the UDP transport for both Master and Slave so that it is some way approaching robust
* Fixed the code layout - everything has headers, the layout is consistent, variable names and scope make sense etc.
* Build system incorporates GPG signing and is compatible with one-click deployment to Maven Central through Sonatype