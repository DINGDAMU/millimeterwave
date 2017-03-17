package org.onosproject.mmcli;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.cli.net.LinksListCommand;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Link;
import org.onosproject.net.Path;
import org.onosproject.net.topology.LinkWeight;
import org.onosproject.net.topology.Topology;
import org.onosproject.net.topology.TopologyEdge;
import org.onosproject.net.topology.TopologyService;


import java.util.Set;

import static org.onosproject.cli.net.LinksListCommand.compactLinkString;

@Command(scope = "onos", name = "mmwave-paths",
        description = "calculate shortest path with own customized link weight")
public class mmWavePathsCommand extends AbstractShellCommand {
    private static final String SEP = "==>";
    @Argument(index = 0, name = "src", description = "Source device ID",
            required = true, multiValued = false)
    String srcArg = null;

    @Argument(index = 1, name = "dst", description = "Destination device ID",
            required = true, multiValued = false)
    String dstArg = null;


    protected TopologyService service;
    protected Topology topology;
    //In our case we need to use topologyService not pathService
    protected void init() {
        service = get(TopologyService.class);
        topology = service.currentTopology();
    }
    @Override
    protected void execute() {

        init();
        DeviceId src = DeviceId.deviceId(srcArg);
        DeviceId dst = DeviceId.deviceId(dstArg);
        if (srcArg.split("/").length != 1 || dstArg.split("/").length != 1) {
            print("Expected device IDs as arguments");
            return;
        }

       Set<Path> paths = service.getPaths(topology,src,dst,new mmwaveLinkWeight());
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
