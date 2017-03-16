package org.onosproject.millimeterwaveweight;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.ConnectPoint;
import org.onosproject.net.Path;
import org.onosproject.net.topology.LinkWeight;
import org.onosproject.net.topology.PathService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;


@Component(immediate = true)
public class MMwaveWeight {

    //<onos.app.name>
    protected static final String APP_NAME = "org.onosproject.millimeterwaveweight";


    //SLF4J
    private final Logger log = LoggerFactory.getLogger(getClass());


    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected PathService pathService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

    private ApplicationId appId;


    @Activate
    protected void activate() {
        appId= coreService.registerApplication(APP_NAME);

        log.info("Started");
    }

    @Deactivate
    protected void deactivate() {

        log.info("Stopped");
    }


    String srcArg= "of:000000000000000e/5";
    String dstArg = "of:000000000000000f/3";
    ConnectPoint src = ConnectPoint.deviceConnectPoint(srcArg);
    ConnectPoint dst = ConnectPoint.deviceConnectPoint(dstArg);
    Path path= getPath(src,dst);







    public Path getPath(ConnectPoint src, ConnectPoint dst) {
        Set<Path> paths = pathService.getPaths(src.deviceId(), dst.deviceId(), new mmwaveLinkWeight());
        if (paths.isEmpty()) {
            log.warn("Unable to find multi-layer path.");
            return null;
        }
        for (Path path : paths) {
            if (path != null) {
                log.info("The paths are {}:", path);
                return path;
            }
        }
        return null;
    }

}







