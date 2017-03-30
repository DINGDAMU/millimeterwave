package org.onosproject.mmcli;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Preconditions;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.cli.net.LinksListCommand;
import org.onosproject.net.Host;
import org.onosproject.net.HostId;
import org.onosproject.net.Link;
import org.onosproject.net.Path;
import org.onosproject.net.host.HostService;
import org.onosproject.net.topology.LinkWeight;
import org.onosproject.net.topology.PathService;
import org.onosproject.net.topology.TopologyEdge;


import java.util.Set;

import static org.onosproject.cli.net.LinksListCommand.compactLinkString;

/**
 * Created by dingdamu on 2017/3/17.
 */
@Command(scope = "onos", name = "mmwave-hosts-paths",
        description = "calculate shortest path between hosts with own customized link weight")
public class mmWaveHostsPathsCommand extends AbstractShellCommand{
    private static final String SEP = "==>";
    @Argument(index = 0, name = "src", description = "Source device ID",
            required = true, multiValued = false)
    String srcArg = null;

    @Argument(index = 1, name = "dst", description = "Destination device ID",
            required = true, multiValued = false)
    String dstArg = null;


    protected PathService pathService;
    protected HostService hostService;
    //In our case we need to use pathService (ElementID is more comfortable than DeviceID in Topology.getPath() case)
    protected void init() {
        pathService = get(PathService.class);
        hostService = get(HostService.class);
    }
    @Override
    protected void execute() {

        init();
        HostId src = HostId.hostId(srcArg);
        HostId dst = HostId.hostId(dstArg);
        Host srchost = hostService.getHost(src);
        Host dsthost = hostService.getHost(dst);
        Set<Path> paths = pathService.getPaths(srchost.location().deviceId(), dsthost.location().deviceId(), new MMwaveLinkWeight());
        if(paths.isEmpty()){
            print("The path is empty!");
            return;
        }
        if (outputJson()) {
            print("%s", json(this, paths));
        } else {
            for (Path path : paths) {
                print(pathString(path));
            }
        }
    }
    /**
     * Produces a JSON array containing the specified paths.
     *
     * @param context context to use for looking up codecs
     * @param paths collection of paths
     * @return JSON array
     */
    public static JsonNode json(AbstractShellCommand context, Iterable<Path> paths) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode result = mapper.createArrayNode();
        for (Path path : paths) {
            result.add(LinksListCommand.json(context, path)
                               .put("cost", path.cost())
                               .set("links", LinksListCommand.json(context, path.links())));
        }
        return result;
    }

    /**
     * Produces a formatted string representing the specified path.
     *
     * @param path network path
     * @return formatted path string
     */
    protected String pathString(Path path) {
        StringBuilder sb = new StringBuilder();
        for (Link link : path.links()) {
            sb.append(compactLinkString(link)).append(SEP);
        }
        sb.delete(sb.lastIndexOf(SEP), sb.length());
        sb.append("; cost=").append(path.cost());
        return sb.toString();
    }

    class MMwaveLinkWeight implements LinkWeight {

        @Override
        public double weight(TopologyEdge edge) {

            //AnnotationKeys
            //This can help us to define cost function by annotations
            String v = edge.link().annotations().value("length");


            try {

                if(v != null){
                    Psuccess psuccess = new Psuccess();
                    double ps = psuccess.getPs(Double.parseDouble(v));
                    return 1+1/ps;
                }else{
                    return 101;
                }
                //total cost = fixed cost + dynamic cost
                // In Ethernet case, total cost = 100 + 1; (ps = 1)
                // In mm-wave case, total cost = 1 + 1/ps;
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }
}
