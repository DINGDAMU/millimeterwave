# Millimeterwave_onos_app
A millimeterwave application based on onos

<img src="https://github.com/DINGDAMU/millimeterwave-onos-app/blob/master/architecture%20overview.png" width="50%" height="50%" />
# Prerequisites
- Java 8 JDK (Oracle Java recommended; OpenJDK is not as thoroughly tested)
- Apache Maven 3.3.9
- git
- bash (for packaging & testing)
- Apache Karaf 3.0.5
- ONOS (git clone https://gerrit.onosproject.org/onos)  
----->More information you can find [here](https://wiki.onosproject.org/display/ONOS/Installing+and+Running+ONOS)


# Installation 
    git clone https://github.com/DINGDAMU/millimeterwave-onos-app.git
    cd millimeterwave-onos-app
    mvn clean install
    cd cli
    mvn clean install
    onos-app localhost reinstall! target/*.oar
    cd ../millimeterwavelink
    mvn clean install 
    onos-app localhost reinstall! target/*.oar
    cd ../millimeterwaveport
    mvn clean install 
    onos-app localhost reinstall! target/*.oar

    
 
# Usage 
## This application can acquire the mininet's topology from different subsystems via northbound APIs, such as HostService, LinkService and DeviceService.  
### Show all components by default
    onos>showcomponets  
### Show only devices
    onos>showcomponets -d  
### Show only links
    onos>showcomponets -l  
### Show only hosts
    onos>showcomponets -h  
    
## This application can also add addtional annotations on devices, links and ports by commands.


### Add additional annotations on devices
    onos>annotate-devices <deviceID> <key> <value>  
   
### Add additional annotations on links
    onos>annotate-links <source-connectPoint> <destination-connectPoint> <key> <value>
    
### Add additional annotations on ports
    onos>annotate-ports <deviceID> <Port number> <Port state> <key> <value>
    
## Use JSON files to annotate millimeterwave links and port 
### A JSON example  
    {
     "apps" : {
    "org.onosproject.millimeterwavelink" : {
      "links" : [{
        "src":"of:000000000000000e/5",
        "dst":"of:000000000000000f/3",
        "length": "100",
        "capacity":"100",
        "technology":"mmwave",
        "ps":"0.86"
      }]
    },
    "org.onosproject.millimeterwaveport" : {
      "ports" : [{
        "technology":"mmwave",
        "deviceID": "of:000000000000000a",
        "portnumber":"1",
        "isEnabled":"true"
      }]
     }
    }
### Configuration  
    onos>onos-netcfg <ONOS's address> <path to JSON> 

