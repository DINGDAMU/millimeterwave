package org.onosproject.mmcli;

import org.onosproject.cli.AbstractShellCommand;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.net.ConnectPoint;
import org.onosproject.net.DefaultAnnotations;
import org.onosproject.net.Link;
import org.onosproject.net.link.DefaultLinkDescription;
import org.onosproject.net.link.LinkDescription;
import org.onosproject.net.link.LinkProvider;
import org.onosproject.net.link.LinkProviderRegistry;
import org.onosproject.net.link.LinkProviderService;
import org.onosproject.net.link.LinkService;
import org.onosproject.net.provider.AbstractProvider;
import org.onosproject.net.provider.ProviderId;


@Command(scope = "onos", name = "annotate-links",
        description = "Annotates links")
public class AnnotateLinkCommand extends AbstractShellCommand {


    static final ProviderId PID = new ProviderId("cli", "org.onosproject.cli", true);

    @Argument(index = 0, name = "srcArg", description = "source connection point",
            required = true, multiValued = false)
    String srcArg = null;


    @Argument(index = 1, name = "dstArg", description = "destination connection point",
            required = true, multiValued = false)
    String dstArg = null;


    @Argument(index = 2, name = "key", description = "Annotation key",
            required = true, multiValued = false)
    String key = null;

    @Argument(index = 3, name = "value",
            description = "Annotation value (null to remove)",
            required = false, multiValued = false)
    String value = null;



    @Override
    protected void execute() {

        LinkProviderRegistry registry = get(LinkProviderRegistry.class);
        LinkProvider provider = new AnnotationProvider();
        ConnectPoint src = ConnectPoint.deviceConnectPoint(srcArg);
        ConnectPoint dst = ConnectPoint.deviceConnectPoint(dstArg);


        try {
            LinkProviderService providerService = registry.register(provider);
            providerService.linkDetected(description(src,dst,key,value));
        } finally {
            registry.unregister(provider);
        }
    }

    private LinkDescription description(ConnectPoint src,ConnectPoint dst, String key, String value) {
        DefaultAnnotations.Builder builder = DefaultAnnotations.builder();

        if (value != null) {
            builder.set(key, value);
        } else {
            builder.remove(key);
        }

        //Use source and destination connection port to define the specific link
        LinkService service = get(LinkService.class);
        Link link = service.getLink(src, dst);
        return new DefaultLinkDescription(src,dst,link.type(),builder.build());

    }


    // Token provider entity
    private static final class AnnotationProvider extends AbstractProvider implements LinkProvider {
        private AnnotationProvider() {
            super(PID);
        }


    }





}




