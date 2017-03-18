package org.onosproject.millimeterwaveintent.cli;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.cli.net.ConnectivityIntentCommand;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Host;
import org.onosproject.net.HostId;
import org.onosproject.net.Path;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.host.HostService;
import org.onosproject.net.intent.Constraint;
import org.onosproject.net.intent.HostToHostIntent;
import org.onosproject.net.intent.Intent;
import org.onosproject.net.intent.IntentService;
import org.onosproject.net.intent.PathIntent;
import org.onosproject.net.topology.LinkWeight;
import org.onosproject.net.topology.PathService;
import org.onosproject.net.topology.TopologyEdge;

import java.util.List;
import java.util.Set;

import static org.onosproject.net.flow.DefaultTrafficSelector.builder;

/**
 * Created by dingdamu on 2017/3/17.
 */
@Command(scope = "onos", name = "add-mmwave-intent",
        description = "Installs mm-wave intents")
public class AddmmWaveIntentCommand extends ConnectivityIntentCommand {
    @Argument(index = 0, name = "src_host", description = "One host ID",
            required = true, multiValued = false)
    String srcArg = null;

    @Argument(index = 1, name = "dst_host" +
            "]", description = "Another host ID",
            required = true, multiValued = false)
    String dstArg = null;

    protected PathService pathService;
    protected HostService hostService;
    //In our case we need to use pathService
    protected void init() {
        pathService=get(PathService.class);
        hostService =get(HostService.class);
    }

    @Override
    protected void execute() {
        init();
        IntentService service = get(IntentService.class);

        HostId src = HostId.hostId(srcArg);
        HostId dst = HostId.hostId(dstArg);

        TrafficSelector selector = buildTrafficSelector();
        TrafficTreatment treatment = buildTrafficTreatment();
        List<Constraint> constraints = buildConstraints();

        HostToHostIntent intent = HostToHostIntent.builder()
                .appId(appId())
                .key(key())
                .one(src)
                .two(dst)
                .selector(selector)
                .treatment(treatment)
                .constraints(constraints)
                .priority(priority())
                .build();

        Set<Path> paths = pathService.getPaths(src,dst,new mmwaveLinkWeight());
        Host srchost = hostService.getHost(src);
        Host dsthost = hostService.getHost(dst);
        for (Path path:paths
             ) {
            Intent pathIntent = createPathIntent(path,srchost,dsthost,intent);
            service.submit(pathIntent);
            print("Host to Host intent submitted:\n%s", intent.toString());
        }
    }
    // Creates a path intent from the specified path and original connectivity intent.
    private Intent createPathIntent(Path path, Host src, Host dst,
                                    HostToHostIntent intent) {
        TrafficSelector selector = builder(intent.selector())
                .matchEthSrc(src.mac()).matchEthDst(dst.mac()).build();
        return PathIntent.builder()
                .appId(intent.appId())
                .key(intent.key())
                .selector(selector)
                .treatment(intent.treatment())
                .path(path)
                .constraints(intent.constraints())
                .priority(intent.priority())
                .build();
    }

    class mmwaveLinkWeight implements LinkWeight {

        @Override
        public double weight(TopologyEdge edge) {

            //AnnotationKeys
            //This can help us to define cost function by annotations
            String v = edge.link().annotations().value("ps");
            try {
                return v != null ? 1+(1/(Double.parseDouble(v) /100)): 101;
                //total cost = fixed cost + dynamic cost
                // In Ethernet case, total cost = 100 + 1; (ps = 1)
                // In mm-wave case, total cost = 1 + 1/ps;
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }
}
