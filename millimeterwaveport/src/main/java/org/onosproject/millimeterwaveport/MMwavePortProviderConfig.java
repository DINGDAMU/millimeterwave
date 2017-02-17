package org.onosproject.millimeterwaveport;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;
import org.onosproject.core.ApplicationId;
import org.onosproject.incubator.net.config.basics.ConfigException;
import org.onosproject.net.config.Config;

import java.util.Set;


public class MMwavePortProviderConfig extends Config<ApplicationId> {
    public static final String CONFIG_VALUE_ERROR = "Error parsing config value";
    private static final String TECHNOLOGY = "technology";
    private static final String DeviceID = "deviceID";
    private static final String PORTNUMBER = "portnumber";
    private static final String ISENABLED="isEnabled";




    public Set<PortAttributes> getPortAttributes() throws ConfigException {
        Set<PortAttributes> portAttributes = Sets.newHashSet();

        try {
            for (JsonNode node : array) {
                String technology = node.path(TECHNOLOGY).asText();
                String devicdID=node.path(DeviceID).asText();
                int portnumber=node.path(PORTNUMBER).asInt();
                boolean isEnabled=node.path(ISENABLED).asBoolean();
                portAttributes.add(new PortAttributes(technology,devicdID,portnumber,isEnabled));

            }
        } catch (IllegalArgumentException e) {
            throw new ConfigException(CONFIG_VALUE_ERROR, e);
        }

        return portAttributes;
    }
    public class PortAttributes {
        private final String technology;
        private final String deviceID;
        private final int portnumber;
        private final boolean isEnabled;


        public PortAttributes(String technology,String deviceID,int portnumber,boolean isEnabled) {
            this.technology = technology;
            this.deviceID=deviceID;
            this.portnumber=portnumber;
            this.isEnabled=isEnabled;

        }



        public String getTechnology() {
            return technology;
        }
        public String getDeviceID(){
            return deviceID;
        }
        public int getPortnumber(){
            return portnumber;
        }
        public boolean getIsEnabled(){
            return isEnabled;
        }
    }

}
