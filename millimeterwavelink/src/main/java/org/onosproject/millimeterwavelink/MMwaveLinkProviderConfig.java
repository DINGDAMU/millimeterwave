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
    private static final String SRC = "src";
    private static final String DST = "dst";


    public Set<LinkAttributes> getLinkAttibutes() throws ConfigException {
        Set<LinkAttributes> linkAttributes = Sets.newHashSet();

        try {
            for (JsonNode node : array) {
                String src = node.path(SRC).asText();
                String dst = node.path(DST).asText();
                long length = node.path(LENGTH).asLong();
                linkAttributes.add(new LinkAttributes(length, src, dst));


            }
        } catch (IllegalArgumentException e) {
            throw new ConfigException(CONFIG_VALUE_ERROR, e);
        }

        return linkAttributes;
    }


    public class LinkAttributes {
        private final long length;
        private final String src;
        private final String dst;


        public LinkAttributes(long length, String src, String dst) {
            this.length = length;
            this.src = src;
            this.dst = dst;

        }


        public long getLength() {
            return length;
        }

        public String getSrc() {
            return src;
        }

        public String getDST() {
            return dst;
        }
    }
}
