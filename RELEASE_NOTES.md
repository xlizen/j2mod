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

## Version 2.0-rc2
* Added ability to specify timeout for Facade classes 'ModbusTCPMaster', 'ModbusUDPMaster' and 'ModbusSerialMaster'
* Fixed all the Unit ID checking when running as a Slave (myriad of problems - cannot see how it has ever worked)

## Version 2.0-rc3
* Fixed the timeouts for UDP/TCP and Serial listeners
* Added more test cases

