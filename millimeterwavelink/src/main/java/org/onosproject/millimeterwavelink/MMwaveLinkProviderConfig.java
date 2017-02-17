package org.onosproject.millimeterwavelink;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;
import org.onosproject.core.ApplicationId;
import org.onosproject.incubator.net.config.basics.ConfigException;
import org.onosproject.net.config.Config;

import java.util.Set;

/**
 * Created by dingdamu on 17/1/11.
 */
public class MMwaveLinkProviderConfig extends Config<ApplicationId> {
    public static final String CONFIG_VALUE_ERROR = "Error parsing config value";
    private static final String LENGTH = "length";
    private static final String CAPACITY = "capacity";
    private static final String TECHNOLOGY = "technology";
    private static final String PS = "ps";
    private static final String SRC = "src";
    private static final String DST = "dst";


    public Set<LinkAttributes> getLinkAttibutes() throws ConfigException {
        Set<LinkAttributes> linkAttributes = Sets.newHashSet();

        try {
            for (JsonNode node : array) {
                String src = node.path(SRC).asText();
                String dst = node.path(DST).asText();
                long length = node.path(LENGTH).asLong();
                long capacity = node.path(CAPACITY).asLong();
                String technology = node.path(TECHNOLOGY).asText();
                String ps = node.path(PS).asText();
                linkAttributes.add(new LinkAttributes(length,capacity,technology,ps, src, dst));


            }
        } catch (IllegalArgumentException e) {
            throw new ConfigException(CONFIG_VALUE_ERROR, e);
        }

        return linkAttributes;
    }


    public class LinkAttributes {
        private final long length;
        private final long capacity;
        private final String technology;
        private final String ps;
        private final String src;
        private final String dst;


        public LinkAttributes(long length, long capacity,String technology,String ps,String src, String dst) {
            this.length = length;
            this.capacity = capacity;
            this.technology = technology;
            this.ps = ps;
            this.src = src;
            this.dst = dst;

        }


        public long getLength() {
            return length;
        }

        public long getCapacity(){
            return capacity;
        }

        public String getTechnology() {
            return technology;
        }

        public String getPs() {
            return ps;
        }

        public String getSrc() {
            return src;
        }

        public String getDST() {
            return dst;
        }
    }
}
